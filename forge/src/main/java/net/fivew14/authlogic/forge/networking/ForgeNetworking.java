package net.fivew14.authlogic.forge.networking;

import com.mojang.logging.LogUtils;
import net.fivew14.authlogic.AuthLogic;
import net.fivew14.authlogic.forge.AuthLogicForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(modid = AuthLogic.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeNetworking {
    private static final String PROTOCOL = "1";

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            AuthLogic.NETWORKING_CHANNEL_ID, () -> PROTOCOL, (a) -> true, (a) -> true
    );

    public static void bootstrap() {
        int id = 0;

        LogUtils.getLogger().info("Register {} -> {}", id, S2CLoginQuery.class.getName());
        S2CLoginQuery.register(CHANNEL, id++);

        LogUtils.getLogger().info("Register {} -> {}", id, C2SQueryResponse.class.getName());
        C2SQueryResponse.register(CHANNEL, id++);

        LogUtils.getLogger().info("Done register count={}", id);
    }


}
