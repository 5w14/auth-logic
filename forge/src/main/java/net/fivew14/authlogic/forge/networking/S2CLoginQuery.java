package net.fivew14.authlogic.forge.networking;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.fivew14.authlogic.client.ClientNetworking;
import net.fivew14.authlogic.server.ServerNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.compress.utils.ByteUtils;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class S2CLoginQuery implements IntSupplier {
    public static void register(SimpleChannel channel, int packetId) {
        channel.messageBuilder(S2CLoginQuery.class, packetId, NetworkDirection.LOGIN_TO_CLIENT)
                .encoder(S2CLoginQuery::encode)
                .decoder(S2CLoginQuery::decode)
                .consumerNetworkThread(S2CLoginQuery::handle)
//                .loginIndex(S2CLoginQuery::getLoginIndex, S2CLoginQuery::setLoginIndex)
//                .markAsLoginPacket()
                .add();
    }

    private int loginIndex; // injected
    private final FriendlyByteBuf payload;

    // Server-side constructor
    public S2CLoginQuery(FriendlyByteBuf payload) {
        this.payload = payload;
    }

    // Client-side decode constructor (or factory)
    private S2CLoginQuery(int loginIndex, FriendlyByteBuf payload) {
        this.loginIndex = loginIndex;
        this.payload = payload;
    }

    public int getLoginIndex() {
        return loginIndex;
    }

    public void setLoginIndex(int loginIndex) {
        this.loginIndex = loginIndex;
    }

    public static void encode(S2CLoginQuery msg, FriendlyByteBuf buf) {
        // write payload bytes in a framed way
        buf.writeVarInt(msg.payload.readableBytes());
        buf.writeBytes(msg.payload, msg.payload.readerIndex(), msg.payload.readableBytes());
    }

    public static S2CLoginQuery decode(FriendlyByteBuf buf) {
        int len = buf.readVarInt();
        FriendlyByteBuf payload =
                new FriendlyByteBuf(buf.readBytes(len));
        return new S2CLoginQuery(-1, payload);
    }

    public static void handle(S2CLoginQuery msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            FriendlyByteBuf response = ClientNetworking.handleLoginQuery(msg.payload);
//            ForgeNetworking.sendLoginReply(msg.getLoginIndex(), response);
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public int getAsInt() {
        return this.loginIndex;
    }
}
