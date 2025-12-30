package net.fivew14.authlogic.server;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;

public class AuthLogicDedicated {
    private static boolean isRunningDedicated;
    private static boolean isActive = false;
    private static MinecraftServer server = null;

    public static void onDedicatedStartup() {
        isRunningDedicated = true;
        LifecycleEvent.SERVER_STARTED.register(AuthLogicDedicated::serverStarting);
    }

    private static void serverStarting(MinecraftServer server) {
        AuthLogicDedicated.server = server;

        if (server.usesAuthentication()) {
            LogUtils.getLogger().error("This server is running in secure connection mode which disables the functionality of AuthLogic.");
            LogUtils.getLogger().error("Please update your server.properties value of online-mode to false.");
        }

        isActive = !server.usesAuthentication();
    }

    public static boolean isRunningDedicated() {
        return isRunningDedicated;
    }

    public static boolean isActive() {
        return isActive;
    }
}
