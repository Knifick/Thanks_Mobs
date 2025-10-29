package net.knifick.praporupdate.network.payloads;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record StopNarratorMusicPayload(int entityId) implements CustomPacketPayload {
    public static final Type<StopNarratorMusicPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("prapor", "stop_narrator_music"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StopNarratorMusicPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, StopNarratorMusicPayload::entityId,
                    StopNarratorMusicPayload::new
            );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}