package net.fivew14.authlogic.neoforge.networking;

import net.fivew14.authlogic.AuthLogic;
import net.fivew14.authlogic.protocol.AuthPayloadData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record S2CLoginQuery(AuthPayloadData data) implements CustomPacketPayload {
    public static final Type<S2CLoginQuery> TYPE = new Type<>(AuthLogic.id("login_query"));
    public static final StreamCodec<FriendlyByteBuf, S2CLoginQuery> STREAM_CODEC = StreamCodec.of(S2CLoginQuery::write, S2CLoginQuery::read);

    public S2CLoginQuery(FriendlyByteBuf buf) {
        this(new AuthPayloadData(buf));
    }

    public FriendlyByteBuf toBuf() {
        return data.toBuf();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void write(FriendlyByteBuf buf, S2CLoginQuery payload) {
        payload.data.write(buf);
    }

    private static S2CLoginQuery read(FriendlyByteBuf buf) {
        return new S2CLoginQuery(AuthPayloadData.read(buf));
    }
}
