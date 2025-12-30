package net.fivew14.authlogic.client;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.fivew14.authlogic.crypto.KeysProvider;
import net.minecraft.client.Minecraft;

public class AuthLogicClient {


    public static void onClientInit() {
        LogUtils.getLogger().info("Hello, client!");
        ClientLifecycleEvent.CLIENT_STARTED.register(AuthLogicClient::onClientStarted);
    }

    private static void onClientStarted(Minecraft minecraft) {
        Minecraft.getInstance().getProfileKeyPairManager().prepareKeyPair().whenComplete((o, e) -> {
            if (e != null || o.isEmpty()) {
                // TODO: Handle offline-mode
            } else {
                // TODO: Handle online-mode
            }
        });
    }
}
