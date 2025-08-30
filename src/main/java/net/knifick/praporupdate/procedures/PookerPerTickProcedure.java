package net.knifick.praporupdate.procedures;

import net.knifick.praporupdate.entity.PookerEntity;
import net.knifick.praporupdate.event.pooker.LookAtPookerHandler;
import net.knifick.praporupdate.item.GuideBookItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;

import java.util.Comparator;

public class PookerPerTickProcedure {

	private static final ResourceLocation POOKER_SOUND = ResourceLocation.parse("prapor:pooker_dissapear");

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null) return;

		spawnIdleParticles(world, x, y, z);

		// Удаляем сущность, если день и нет грозы
		if (shouldVanishInDay(world)) {
			spawnSmokeBurst(world, x, y + 1.5, z);
			discardEntity(entity);
			return;
		}

		Player nearestPlayer = findNearestPlayer(world, x, y, z, 20);
		if (nearestPlayer != null && !isSpectator(nearestPlayer)) {
			handlePlayerInteraction(nearestPlayer, entity);
		}
	}

	/** Эффекты, которые идут постоянно */
	private static void spawnIdleParticles(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel level) {
			level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 20, 0.5, 0.5, 0.5, 0.01);
			level.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 3, 3, 3, 0.05);
		}
	}

	/** Проверка: сущность должна исчезнуть днём */
	private static boolean shouldVanishInDay(LevelAccessor world) {
		if (world instanceof Level level) {
			return level.isDay() && !world.getLevelData().isThundering();
		}
		return false;
	}

	/** Эффект исчезновения */
	public static void spawnSmokeBurst(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel level) {
			level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 600, 1, 1, 1, 0);
		}
	}

	/** Находим ближайшего игрока */
	private static Player findNearestPlayer(LevelAccessor world, double x, double y, double z, double range) {
		return world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), range, range, range))
				.stream()
				.min(Comparator.comparingDouble(e -> e.distanceToSqr(x, y, z)))
				.orElse(null);
	}

	/** Проверка на режим "Наблюдатель" */
	private static boolean isSpectator(Entity entity) {
		if (entity instanceof ServerPlayer sp) {
			return sp.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
		}
		if (entity.level().isClientSide() && entity instanceof Player p) {
			var info = Minecraft.getInstance().getConnection().getPlayerInfo(p.getGameProfile().getId());
			return info != null && info.getGameMode() == GameType.SPECTATOR;
		}
		return false;
	}

	/** Действия, если игрок подошёл к Pooker */
	public static void handlePlayerInteraction(Player player, Entity entity) {
		if (entity.tickCount % 10 == 0 && !player.level().isClientSide) {
			double distance = player.distanceTo(entity);

			double maxDistance = 20.0;
			float maxDamage = 10.0f;
			double clamped = Math.min(distance, maxDistance);
			float damage = (float) (maxDamage * (1.0 - (clamped / maxDistance)));

			if (damage > 0) {
				if(player.getHealth()-damage>0)
					player.hurt(entity.damageSources().magic(), damage);
				else if(!player.isCreative()){
					LookAtPookerHandler.screamer((PookerEntity) entity, player);
				}
			}
			if (player.level() instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
						player.getX(), player.getY()+1, player.getZ(),
						50,
						1, 1, 1,
						0
				);
			}

		}
	}

	/** Воспроизведение звука (учитываем клиент/сервер) */
	private static void playSound(LevelAccessor world, double x, double y, double z, ResourceLocation sound) {
		if (world instanceof Level level) {
			if (!level.isClientSide()) {
				level.playSound(null, BlockPos.containing(x, y, z),
						BuiltInRegistries.SOUND_EVENT.get(sound),
						SoundSource.MASTER, 1, 1);
			} else {
				level.playLocalSound(x, y, z,
						BuiltInRegistries.SOUND_EVENT.get(sound),
						SoundSource.MASTER, 1, 1, false);
			}
		}
	}

	/** Удаление сущности */
	private static void discardEntity(Entity entity) {
		if (!entity.level().isClientSide()) {
			entity.discard();
		}
	}
}
