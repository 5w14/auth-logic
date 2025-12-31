package net.fivew14.authlogic.client;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

public class ClientNetworking {
    // handle & validate server query
    public static FriendlyByteBuf handleLoginQuery(FriendlyByteBuf buf) {
        // TO VALIDATE:
        // - nonce
        // - temp key

        // TO SEND:
        // - client uuid+username (we need it as forge/fabric apis are not same)
        // - temp client key
        // - client nonce
        // - ECDH encrypted blob of offline/online authentication data
        //   - client public key
        //   - signature ( client nonce, server nonce, client temp key, server temp key )

        buf.readUtf();
        return new FriendlyByteBuf(Unpooled.buffer()).writeUtf("utf8");
    }
}
