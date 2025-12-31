package net.fivew14.authlogic.server;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ServerNetworking {
    public static FriendlyByteBuf getServerQuery() {
//        var reference = ServerAuthState.newAuthState();

        // TO SEND:
        // - temp key
        // - const key
        // - nonce
        // - signature (tempkey + nonce)

        return new FriendlyByteBuf(Unpooled.buffer()).writeUtf("test");
    }

    public static void validateClientResponse(FriendlyByteBuf buf, UsernameGetter usernameGetter) {
        buf.readUtf();
//        var type = new ResourceLocation(buf.readUtf(), buf.readUtf());
    }

    @FunctionalInterface
    public interface UsernameGetter {
        String getUsername();
    }
}
