package net.knifick.praporupdate.util.narrator;

import net.knifick.praporupdate.entity.NarratorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.lang.ref.WeakReference;

public class NarratorMusicInstance extends AbstractTickableSoundInstance {
    private final int entityId;
    private final WeakReference<NarratorEntity> ref;

    public NarratorMusicInstance(SoundEvent sound, NarratorEntity narrator) {
        super(sound, SoundSource.RECORDS, Minecraft.getInstance().level.random);
        this.entityId = narrator.getId();
        this.ref = new WeakReference<>(narrator);

        this.looping = false;             // музыка диска с собственной длительностью
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.attenuation = Attenuation.LINEAR; // естественное затухание по дистанции

        this.x = (float) narrator.getX();
        this.y = (float) narrator.getY();
        this.z = (float) narrator.getZ();
    }

    public int getEntityId() { return entityId; }

    @Override
    public void tick() {
        NarratorEntity e = ref.get();
        if (e == null || !e.isAlive() || !e.isMusic()) {
            this.stop(); // моб исчез / музыка выключена на сервере — стоп
            return;
        }
        this.x = (float) e.getX();
        this.y = (float) e.getY();
        this.z = (float) e.getZ();
    }
}