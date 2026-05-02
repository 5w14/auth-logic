package net.fivew14.authlogic.neoforge;

import net.fivew14.authlogic.AuthLogic;
import net.fivew14.authlogic.client.AuthLogicClient;
import net.fivew14.authlogic.neoforge.networking.NeoForgeNetworking;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(AuthLogic.MOD_ID)
public final class AuthLogicNeoForge {
    public AuthLogicNeoForge(IEventBus modEventBus) {
        AuthLogic.init();

        modEventBus.addListener(this::clientInit);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(NeoForgeNetworking::registerPayloadHandlers);
        modEventBus.addListener(NeoForgeNetworking::registerConfigurationTasks);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NeoForgeNetworking::bootstrap);
    }

    public void clientInit(FMLClientSetupEvent event) {
        AuthLogicClient.onClientInit();
    }
}
