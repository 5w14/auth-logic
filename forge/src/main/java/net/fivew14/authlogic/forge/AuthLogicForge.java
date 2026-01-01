package net.fivew14.authlogic.forge;

import dev.architectury.platform.forge.EventBuses;
import net.fivew14.authlogic.AuthLogic;
import net.fivew14.authlogic.client.AuthLogicClient;
import net.fivew14.authlogic.forge.networking.ForgeNetworking;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AuthLogic.MOD_ID)
public final class AuthLogicForge {
    public AuthLogicForge() {
        EventBuses.registerModEventBus(AuthLogic.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        AuthLogic.init();

        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::clientInit);
        bus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ForgeNetworking::bootstrap);
    }

    public void clientInit(FMLClientSetupEvent event) {
        AuthLogicClient.onClientInit();
    }
}
