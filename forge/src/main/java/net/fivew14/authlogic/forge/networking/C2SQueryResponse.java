package net.fivew14.authlogic.forge.networking;

import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import net.fivew14.authlogic.server.ServerNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class C2SQueryResponse implements IntSupplier {
    public static void register(SimpleChannel channel, int packetId) {
        channel.messageBuilder(C2SQueryResponse.class, packetId, NetworkDirection.LOGIN_TO_SERVER)
                .encoder(C2SQueryResponse::encode).decoder(C2SQueryResponse::decode)
                .consumerNetworkThread(HandshakeHandler.indexFirst(C2SQueryResponse::handle))
                .loginIndex(C2SQueryResponse::getLoginIndex, C2SQueryResponse::setLoginIndex)
                .add();
    }

    private int loginIndex; // injected
    private final FriendlyByteBuf payload;

    public C2SQueryResponse(FriendlyByteBuf payload) {
        this.payload = payload;
    }

    public int getLoginIndex() {
        return loginIndex;
    }

    public void setLoginIndex(int loginIndex) {
        this.loginIndex = loginIndex;
    }

    public static void encode(C2SQueryResponse msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.payload.readableBytes());
        buf.writeBytes(msg.payload, msg.payload.readerIndex(), msg.payload.readableBytes());
    }

    public static C2SQueryResponse decode(FriendlyByteBuf buf) {
        int len = buf.readVarInt();
        FriendlyByteBuf payload = new FriendlyByteBuf(buf.readBytes(len));
        return new C2SQueryResponse(payload);
    }

    public static void handle(HandshakeHandler h, C2SQueryResponse msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
        var sender = ctx.get().getSender();
        if (sender == null) return;
        ServerNetworking.validateClientResponse(
                msg.payload,
                () -> sender.getGameProfile().getName()
        );
    }

    @Override
    public int getAsInt() {
        return this.loginIndex;
    }
}
