package net.knifick.praporupdate.procedures;

import net.knifick.praporupdate.init.PraporModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import net.knifick.praporupdate.network.PraporModVariables;
import net.knifick.praporupdate.init.PraporModMobEffects;
import net.knifick.praporupdate.PraporMod;

public class FrameReturnerProcedure {

	private static final int MAX_FRAME = 10;   // последняя стадия анимации
	private static final int TICK_DELAY = 2;   // задержка между кадрами (в тиках)

	public static void execute(LevelAccessor world, Entity entity, double duration) {
		if (entity == null) return;

		PraporModVariables.PlayerVariables vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);

		// Если анимация не запущена
		if (vars.screamAnimValue == 0) {
			playScreamSound(world, entity);
			applyFearEffect(entity, duration);
			startAnimation(entity);
		} else {
			resetAnimation(entity);
		}
	}

	/** Проигрывание звука крика */
	private static void playScreamSound(LevelAccessor world, Entity entity) {
		if (world instanceof ServerLevel serverLevel) {
			serverLevel.playSound(null,
					entity.blockPosition(),
					PraporModSounds.SCREAM.get(),
					SoundSource.AMBIENT, 1.0f, 1.0f
			);
		}
	}

	/** Наложение эффекта страха */
	private static void applyFearEffect(Entity entity, double duration) {
		if (entity instanceof LivingEntity living && !living.level().isClientSide()) {
			living.addEffect(new MobEffectInstance(
					PraporModMobEffects.FEAR,
					(int) duration,
					0,
					true,
					false
			));
		}
	}

	/** Запуск анимации screamAnimValue от 1 до MAX_FRAME */
	private static void startAnimation(Entity entity) {
		for (int frame = 1; frame <= MAX_FRAME; frame++) {
			final int currentFrame = frame;
			PraporMod.queueServerWork(frame * TICK_DELAY, () -> setAnimValue(entity, currentFrame));
		}

		// Возврат к 0 после окончания цикла
		PraporMod.queueServerWork((MAX_FRAME + 1) * TICK_DELAY, () -> setAnimValue(entity, 0));
	}

	/** Сброс анимации */
	private static void resetAnimation(Entity entity) {
		setAnimValue(entity, 0);
	}

	/** Установка значения screamAnimValue с синхронизацией */
	private static void setAnimValue(Entity entity, int value) {
		PraporModVariables.PlayerVariables vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
		vars.screamAnimValue = value;
		vars.syncPlayerVariables(entity);
	}
}
