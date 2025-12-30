package net.fivew14.authlogic.forge;

import net.fivew14.authlogic.AuthLogic;
import dev.architectury.platform.forge.EventBuses;
import net.fivew14.authlogic.client.AuthLogicClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AuthLogic.MOD_ID)
public final class AuthLogicForge {
    public AuthLogicForge() {
        EventBuses.registerModEventBus(AuthLogic.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        AuthLogic.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
    }

    public void clientInit(FMLClientSetupEvent event) {
        AuthLogicClient.onClientInit();
    }
}
