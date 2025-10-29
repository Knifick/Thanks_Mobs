package net.knifick.praporupdate.util.narrator;

import net.knifick.praporupdate.entity.NarratorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public final class NarratorMusicClient {
    private static final Map<Integer, NarratorMusicInstance> ACTIVE = new HashMap<>();

    private NarratorMusicClient() {}

    public static void start(int entityId, ResourceLocation soundId) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return;

        // Останавливаем предыдущее (если было)
        stop(entityId);

        Entity e = mc.level.getEntity(entityId);
        if (!(e instanceof NarratorEntity narrator)) return;

        SoundEvent event = BuiltInRegistries.SOUND_EVENT.get(soundId);
        if (event == null) return;

        NarratorMusicInstance inst = new NarratorMusicInstance(event, narrator);
        ACTIVE.put(entityId, inst);
        mc.getSoundManager().play(inst);
    }

    public static void stop(int entityId) {
        NarratorMusicInstance prev = ACTIVE.remove(entityId);
        if (prev != null)
            Minecraft.getInstance().getSoundManager().stop(prev);
    }
}