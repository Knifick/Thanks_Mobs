package net.knifick.praporupdate.util.narrator;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

public class CustomSoundInstance extends AbstractTickableSoundInstance {
	private final Entity attachedEntity;
	private boolean shouldStop = false;

	public CustomSoundInstance(SoundEvent soundIn, SoundSource categoryIn, float volume, float pitch, RandomSource random, Entity entity) {
		super(soundIn, categoryIn, random);
		this.attachedEntity = entity;
		this.volume = volume;
		this.pitch = pitch;
		this.looping = true;
		this.delay = 0;
		this.x = entity.getX();
		this.y = entity.getY();
		this.z = entity.getZ();
	}

	@Override
	public void tick() {
		if (shouldStop || attachedEntity == null || attachedEntity.isRemoved()) {
			looping = false;
		} else {
			this.x = attachedEntity.getX();
			this.y = attachedEntity.getY();
			this.z = attachedEntity.getZ();
		}
	}

	@Override
	public boolean canPlaySound() {
		return !shouldStop;
	}

	public void stopSound() {
		this.shouldStop = true;
		this.volume = 0;
		this.looping = false;
	}
}
