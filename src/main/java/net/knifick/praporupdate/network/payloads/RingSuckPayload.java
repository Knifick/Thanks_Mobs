package net.knifick.praporupdate.network.payloads;

import io.netty.buffer.ByteBuf;
import net.knifick.praporupdate.PraporMod;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RingSuckPayload(double x, double y, double z, double dx, double dy, double dz) implements CustomPacketPayload {
    public static final Type<RingSuckPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PraporMod.MODID, "ring"));

    public static final StreamCodec<ByteBuf, RingSuckPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeDouble(payload.x);
                buf.writeDouble(payload.y);
                buf.writeDouble(payload.z);
                buf.writeDouble(payload.dx);
                buf.writeDouble(payload.dy);
                buf.writeDouble(payload.dz);
            },
            buf -> new RingSuckPayload(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
