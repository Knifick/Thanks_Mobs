package net.knifick.praporupdate.network.payloads;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record StartNarratorMusicPayload(int entityId, ResourceLocation soundId) implements CustomPacketPayload {
    public static final Type<StartNarratorMusicPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("prapor", "start_narrator_music"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StartNarratorMusicPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, StartNarratorMusicPayload::entityId,
                    ResourceLocation.STREAM_CODEC, StartNarratorMusicPayload::soundId,
                    StartNarratorMusicPayload::new
            );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}