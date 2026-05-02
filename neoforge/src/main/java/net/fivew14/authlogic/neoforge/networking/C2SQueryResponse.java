package net.fivew14.authlogic.neoforge.networking;

import net.fivew14.authlogic.AuthLogic;
import net.fivew14.authlogic.protocol.AuthPayloadData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SQueryResponse(AuthPayloadData data) implements CustomPacketPayload {
    public static final Type<C2SQueryResponse> TYPE = new Type<>(AuthLogic.id("query_response"));
    public static final StreamCodec<FriendlyByteBuf, C2SQueryResponse> STREAM_CODEC = StreamCodec.of(C2SQueryResponse::write, C2SQueryResponse::read);

    public C2SQueryResponse(FriendlyByteBuf buf) {
        this(new AuthPayloadData(buf));
    }

    public FriendlyByteBuf toBuf() {
        return data.toBuf();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void write(FriendlyByteBuf buf, C2SQueryResponse payload) {
        payload.data.write(buf);
    }

    private static C2SQueryResponse read(FriendlyByteBuf buf) {
        return new C2SQueryResponse(AuthPayloadData.read(buf));
    }
}
