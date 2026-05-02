package net.fivew14.authlogic.protocol;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

/** Raw auth payload bytes shared by platform networking implementations. */
public record AuthPayloadData(byte[] bytes) {
    public AuthPayloadData(FriendlyByteBuf buf) {
        this(copy(buf));
    }

    public FriendlyByteBuf toBuf() {
        return new FriendlyByteBuf(Unpooled.wrappedBuffer(bytes));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public static AuthPayloadData read(FriendlyByteBuf buf) {
        byte[] bytes = new byte[buf.readVarInt()];
        buf.readBytes(bytes);
        return new AuthPayloadData(bytes);
    }

    private static byte[] copy(FriendlyByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return bytes;
    }
}
