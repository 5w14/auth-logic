package net.fivew14.authlogic.forge.networking;

import com.mojang.logging.LogUtils;
import net.fivew14.authlogic.server.ServerNetworking;
import net.fivew14.authlogic.verification.VerificationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Forge server-side handler for client authentication responses.
 * 
 * Authentication state correlation is handled at the protocol level using
 * the server nonce echoed in the client response, so no connection ID
 * management is needed here.
 */
public final class C2SQueryResponse implements IntSupplier {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static void register(SimpleChannel channel, int packetId) {
        channel.messageBuilder(C2SQueryResponse.class, packetId, NetworkDirection.LOGIN_TO_SERVER)
                .encoder(C2SQueryResponse::encode).decoder(C2SQueryResponse::decode)
                .consumerNetworkThread(HandshakeHandler.indexFirst(C2SQueryResponse::handle))
                .loginIndex(C2SQueryResponse::getLoginIndex, C2SQueryResponse::setLoginIndex)
                .add();
    }

    private int loginIndex; // injected
    private final FriendlyByteBuf payload;

    public C2SQueryResponse(FriendlyByteBuf payload) {
        this.payload = payload;
    }

    public int getLoginIndex() {
        return loginIndex;
    }

    public void setLoginIndex(int loginIndex) {
        this.loginIndex = loginIndex;
    }

    public static void encode(C2SQueryResponse msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.payload.readableBytes());
        buf.writeBytes(msg.payload, msg.payload.readerIndex(), msg.payload.readableBytes());
    }

    public static C2SQueryResponse decode(FriendlyByteBuf buf) {
        int len = buf.readVarInt();
        FriendlyByteBuf payload = new FriendlyByteBuf(buf.readBytes(len));
        return new C2SQueryResponse(payload);
    }

    public static void handle(HandshakeHandler h, C2SQueryResponse msg, Supplier<NetworkEvent.Context> ctx) {
        var networkManager = ctx.get().getNetworkManager();
        
        try {
            // Validate client response - correlation is by server nonce in the response
            // Username is extracted from the verified payload, not from sender (which is null during LOGIN)
            ServerNetworking.validateClientResponse(
                msg.payload,
                () -> "unknown" // Username comes from verified payload, this is just for logging
            );
            
            LOGGER.info("Client authenticated successfully");
            ctx.get().setPacketHandled(true);
            
        } catch (VerificationException e) {
            LOGGER.error("Client authentication failed: {}", e.getMessage());
            networkManager.disconnect(Component.literal("Authentication failed: " + e.getMessage()));
            ctx.get().setPacketHandled(false);
        } catch (Exception e) {
            LOGGER.error("Unexpected error during authentication", e);
            networkManager.disconnect(Component.literal("Authentication error: " + e.getMessage()));
            ctx.get().setPacketHandled(false);
        }
    }

    @Override
    public int getAsInt() {
        return this.loginIndex;
    }
}
