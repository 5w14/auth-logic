package net.fivew14.authlogic.forge;

import net.fivew14.authlogic.AuthLogic;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AuthLogic.MOD_ID)
public final class AuthLogicForge {
    public AuthLogicForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(AuthLogic.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        AuthLogic.init();
    }
}
