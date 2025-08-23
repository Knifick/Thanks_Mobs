package net.knifick.praporupdate.network.payloads;

import io.netty.buffer.ByteBuf;
import net.knifick.praporupdate.PraporMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToastPayload(String mob) implements CustomPacketPayload {
    public static final Type<ToastPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PraporMod.MODID, "toast"));

    public static final StreamCodec<ByteBuf, ToastPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                FriendlyByteBuf fbuf = new FriendlyByteBuf(buf);
                fbuf.writeUtf(payload.mob); // теперь доступен writeUtf
            },
            buf -> {
                FriendlyByteBuf fbuf = new FriendlyByteBuf(buf);
                return new ToastPayload(fbuf.readUtf(32767));
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
