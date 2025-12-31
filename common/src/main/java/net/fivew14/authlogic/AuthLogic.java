package net.fivew14.authlogic;

import com.mojang.logging.LogUtils;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.fivew14.authlogic.client.AuthLogicClient;
import net.fivew14.authlogic.crypto.KeysProvider;
import net.fivew14.authlogic.server.AuthLogicDedicated;
import net.fivew14.authlogic.server.ServerNetworking;
import net.fivew14.authlogic.server.ServerStorage;
import net.fivew14.authlogic.verification.VerificationRegistry;
import net.fivew14.authlogic.verification.codecs.OfflineVerificationCodec;
import net.fivew14.authlogic.verification.codecs.OnlineVerificationCodec;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

/**
 * Main mod class for AuthLogic.
 * Handles initialization and registration of authentication system components.
 */
public final class AuthLogic {
    public static final String MOD_ID = "authlogic";
    public static final ResourceLocation NETWORKING_CHANNEL_ID = AuthLogic.id("authlogin");
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static ServerStorage serverStorage;

    /**
     * Initializes the mod.
     * Called by platform-specific loaders (Fabric/Forge).
     */
    public static void init() {
        LOGGER.info("Initializing AuthLogic");
        
        // Bootstrap crypto providers
        KeysProvider.bootstrap();
        
        // Register built-in verification codecs
        registerVerificationCodecs();
        
        // Initialize server storage on dedicated server
        // Client storage is initialized separately by AuthLogicClient.onClientInit()
        if (Platform.getEnvironment() == Env.SERVER) {
            initServerStorage();
            AuthLogicDedicated.onDedicatedStartup();
        }
        
        LOGGER.info("AuthLogic initialized successfully with {} verification codecs",
            VerificationRegistry.getRegisteredTypes().size());
    }
    
    /**
     * Registers built-in verification codecs.
     */
    private static void registerVerificationCodecs() {
        // Register offline mode codec
        VerificationRegistry.register(
            new ResourceLocation(MOD_ID, "offline"),
            new OfflineVerificationCodec()
        );
        
        // Register online mode codec
        VerificationRegistry.register(
            new ResourceLocation(MOD_ID, "online"),
            new OnlineVerificationCodec()
        );
        
        LOGGER.info("Registered verification codecs: offline, online");
    }
    
    /**
     * Initializes server-side storage.
     */
    private static void initServerStorage() {
        try {
            serverStorage = new ServerStorage();
            serverStorage.load();
            ServerNetworking.setStorage(serverStorage);
            LOGGER.info("Server storage initialized");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize server storage", e);
            throw new RuntimeException("Failed to initialize server storage", e);
        }
    }
    
    /**
     * Gets the server storage instance.
     * 
     * @return Server storage
     * @throws IllegalStateException if called on client or not initialized
     */
    public static ServerStorage getServerStorage() {
        if (serverStorage == null) {
            throw new IllegalStateException("Server storage not initialized or running on client");
        }
        return serverStorage;
    }

    public static boolean isOnDedicated() {
        return AuthLogicDedicated.isRunningDedicated();
    }

    public static ResourceLocation id(String location) {
        return new ResourceLocation(MOD_ID, location);
    }
}
