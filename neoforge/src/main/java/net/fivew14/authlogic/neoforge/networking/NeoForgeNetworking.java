package net.fivew14.authlogic.neoforge.networking;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import net.fivew14.authlogic.AuthLogic;
import net.fivew14.authlogic.client.AuthLogicClient;
import net.fivew14.authlogic.server.ServerNetworking;
import net.fivew14.authlogic.verification.VerificationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class NeoForgeNetworking {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ConfigurationTask.Type AUTH_TASK = new ConfigurationTask.Type(AuthLogic.MOD_ID + ":auth");

    private NeoForgeNetworking() {
    }

    public static void bootstrap() {
        LOGGER.debug("AuthLogic NeoForge networking bootstrapped");
    }

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1").executesOn(HandlerThread.NETWORK);
        registrar.configurationToClient(S2CLoginQuery.TYPE, S2CLoginQuery.STREAM_CODEC, NeoForgeNetworking::handleChallenge);
        registrar.configurationToServer(C2SQueryResponse.TYPE, C2SQueryResponse.STREAM_CODEC, NeoForgeNetworking::handleResponse);
    }

    public static void registerConfigurationTasks(RegisterConfigurationTasksEvent event) {
        if (AuthLogic.isIntegratedServer()) {
            LOGGER.debug("Skipping authentication query for integrated server");
            return;
        }

        event.register(new AuthConfigurationTask());
    }

    private static void handleChallenge(S2CLoginQuery payload, IPayloadContext context) {
        try {
            String serverAddress = getServerAddress(context);
            FriendlyByteBuf response = AuthLogicClient.handleServerChallenge(payload.toBuf(), serverAddress);
            context.reply(new C2SQueryResponse(response));
            response.release();
        } catch (VerificationException e) {
            LOGGER.error("Authentication failed: {}", e.getMessage());
            context.disconnect(e.getVisualError());
        } catch (Exception e) {
            LOGGER.error("Unexpected error during authentication", e);
            context.disconnect(Component.literal("Authentication error: " + e.getMessage()));
        }
    }

    private static void handleResponse(C2SQueryResponse payload, IPayloadContext context) {
        FriendlyByteBuf buf = payload.toBuf();
        try {
            byte[] responseData = new byte[buf.readableBytes()];
            buf.readBytes(responseData);
            String expectedUsername = getExpectedUsername(context);

            CompletableFuture.runAsync(() -> {
                FriendlyByteBuf bufCopy = new FriendlyByteBuf(Unpooled.wrappedBuffer(responseData));
                try {
                    ServerNetworking.validateClientResponse(bufCopy, expectedUsername);
                    LOGGER.debug("Client authenticated successfully: {}", expectedUsername);
                    context.finishCurrentTask(AUTH_TASK);
                } catch (VerificationException e) {
                    LOGGER.error("Client authentication failed: {}", e.getMessage());
                    context.disconnect(Component.literal("Authentication failed: " + e.getMessage()));
                } catch (Exception e) {
                    LOGGER.error("Unexpected error during authentication", e);
                    context.disconnect(Component.literal("Authentication error: " + e.getMessage()));
                } finally {
                    bufCopy.release();
                }
            });
        } catch (Exception e) {
            LOGGER.error("Unexpected error during authentication", e);
            context.disconnect(Component.literal("Authentication error: " + e.getMessage()));
        } finally {
            buf.release();
        }
    }

    private static String getServerAddress(IPayloadContext context) {
        SocketAddress address = context.connection().getRemoteAddress();
        return address != null ? address.toString() : "unknown";
    }

    private static String getExpectedUsername(IPayloadContext context) throws ReflectiveOperationException {
        if (context.listener() instanceof ServerConfigurationPacketListenerImpl listener) {
            Field field = ServerConfigurationPacketListenerImpl.class.getDeclaredField("gameProfile");
            field.setAccessible(true);
            GameProfile profile = (GameProfile) field.get(listener);
            if (profile != null) {
                return sanitizeUsername(profile.getName());
            }
        }
        return sanitizeUsername(context.listener().toString());
    }

    private static String sanitizeUsername(String username) {
        int addressStart = username.indexOf(" (/");
        if (addressStart >= 0) {
            return username.substring(0, addressStart);
        }
        return username;
    }

    private static final class AuthConfigurationTask implements ICustomConfigurationTask {
        @Override
        public Type type() {
            return AUTH_TASK;
        }

        @Override
        public void run(Consumer<net.minecraft.network.protocol.common.custom.CustomPacketPayload> sender) {
            try {
                sender.accept(new S2CLoginQuery(ServerNetworking.getServerQuery()));
                LOGGER.debug("Sent authentication query to client");
            } catch (Exception e) {
                LOGGER.error("Failed to send authentication query", e);
                throw new IllegalStateException("Failed to send authentication query", e);
            }
        }
    }
}
