package net.fivew14.authlogic.fabric.client;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fivew14.authlogic.client.AuthLogicClient;
import net.fivew14.authlogic.fabric.networking.FabricClientNetworking;

public final class AuthLogicFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AuthLogicClient.onClientInit();
        FabricClientNetworking.bootstrap();

        // Defer this, since we don't have the key manager right away
        ClientLifecycleEvent.CLIENT_STARTED.register(AuthLogicClient::onClientStarted);
    }
}
