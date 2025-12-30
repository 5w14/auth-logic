package net.fivew14.authlogic.fabric;

import net.fivew14.authlogic.AuthLogic;
import net.fabricmc.api.ModInitializer;

public final class AuthLogicFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        AuthLogic.init();
    }
}
