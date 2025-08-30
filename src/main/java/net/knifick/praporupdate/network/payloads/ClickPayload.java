package net.knifick.praporupdate.network.payloads;

import io.netty.buffer.ByteBuf;
import net.knifick.praporupdate.PraporMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClickPayload() implements CustomPacketPayload {
    public static final Type<ClickPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PraporMod.MODID, "click"));

    public static final StreamCodec<ByteBuf, ClickPayload> STREAM_CODEC =
            StreamCodec.unit(new ClickPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
