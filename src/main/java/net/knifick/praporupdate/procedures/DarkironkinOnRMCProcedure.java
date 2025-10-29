package net.knifick.praporupdate.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.knifick.praporupdate.PraporMod;
import net.knifick.praporupdate.entity.DarkironkinEntity;
import net.knifick.praporupdate.util.ironkin.ScreenShakeUtil;

import java.util.*;

public class DarkironkinOnRMCProcedure {

	// -------------------- Tuning --------------------
	private static final int   GR_TIMER_TICKS         = 100;
	private static final int   GR_SWITCH_TICKS        = 78;
	private static final int   SM_SWITCH_TICKS        = 84;

	// партиклы
	private static final int    PARTICLE_COUNT        = 900;     // было ~500
	private static final double PARTICLE_RADIUS       = 20.0;    // было 15.0
	private static final double PARTICLE_Y_BASE       = 0.10;
	private static final double PARTICLE_Y_RAND       = 0.15;

	// удар по земле
	private static final double ATTACK_RADIUS         = 20.0;    // было до 16
	private static final double KNOCK_Y_STRONG        = 0.80;
	private static final double KNOCK_Y_MED           = 0.60;
	private static final double KNOCK_Y_LIGHT         = 0.40;
	private static final double KNOCK_Y_TINY          = 0.20;

	// полосы урона/эффектов по дистанции (квадраты, чтобы не брать sqrt)
	private static final double R1 = 6.0;   private static final double R1_2 = R1*R1;   // ближний
	private static final double R2 = 10.0;  private static final double R2_2 = R2*R2;
	private static final double R3 = 14.0;  private static final double R3_2 = R3*R3;
	private static final double R4 = 20.0;  private static final double R4_2 = R4*R4;   // внешний край

	private static final float  DMG_R1 = 20f;
	private static final float  DMG_R2 = 16f;
	private static final float  DMG_R3 = 12f;
	private static final float  DMG_R4 = 8f;

	private static final int SLOW_R1_T = 300, SLOW_R1_A = 2;
	private static final int SLOW_R2_T = 200, SLOW_R2_A = 2;
	private static final int SLOW_R3_T = 150, SLOW_R3_A = 1;
	private static final int SLOW_R4_T = 100, SLOW_R4_A = 1;

	private static final ResourceLocation DS_ID = ResourceLocation.parse("prapor:darkironkinhurt");
	private static final ResourceLocation SND_GROUND = ResourceLocation.parse("prapor:ironkin_ground_hit");
	private static final ResourceLocation SND_SUMMON = ResourceLocation.parse("prapor:summon");

	// ------------------------------------------------

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (!(entity instanceof Mob)) return;

		// большой удар о землю (Ground)
		if (canGroundAttack(entity)) {
			startGroundAttack(entity);
			PraporMod.queueServerWork(27, () -> {
				playSound(world, x, y, z, SND_GROUND, SoundSource.HOSTILE);
				spawnGroundParticles(world, x, z);
				doGroundShockwave(world, x, y, z, entity);
			});
			return;
		}

		// призыв (Summon) — оставлено как у вас, только слегка прибрано
		if (canSummon(entity)) {
			startSummon(entity);
			PraporMod.queueServerWork(42, () -> {
				playSound(world, x, y, z, ResourceLocation.parse("event.raid.horn"), SoundSource.NEUTRAL);
				playSound(world, x, y, z, SND_SUMMON, SoundSource.NEUTRAL);
				spawnWitherSkeletonsAround((LivingEntity) entity, 8, 5, 15);
			});
		}
	}

	// ===================== Ground Attack =====================

	private static boolean canGroundAttack(Entity e) {
		if (!(e instanceof DarkironkinEntity d)) return false;
		if (d.getEntityData().get(DarkironkinEntity.DATA_IsGrAttack)) return false;
		if (d.getEntityData().get(DarkironkinEntity.DATA_IsSmAttack)) return false;
		return d.getEntityData().get(DarkironkinEntity.DATA_GrTimer) == 0;
	}

	private static void startGroundAttack(Entity e) {
		DarkironkinEntity d = (DarkironkinEntity) e;
		d.getEntityData().set(DarkironkinEntity.DATA_GrTimer, GR_TIMER_TICKS);
		d.getEntityData().set(DarkironkinEntity.DATA_IsGrAttack, true);
		d.getEntityData().set(DarkironkinEntity.DATA_GrSwitchTimer, GR_SWITCH_TICKS);
		d.setAnimation("groundHit");
	}

	private static void spawnGroundParticles(LevelAccessor world, double x, double z) {
		if (!(world instanceof ServerLevel server)) return;

		RandomSource rnd = RandomSource.create();
		Map<BlockPos, Integer> heightCache = new HashMap<>();

		for (int i = 0; i < PARTICLE_COUNT; i++) {
			double angle = rnd.nextDouble() * (Math.PI * 2);
			double dist  = rnd.nextDouble() * PARTICLE_RADIUS;

			double px = x + Mth.cos((float) angle) * dist;
			double pz = z + Mth.sin((float) angle) * dist;

			BlockPos col = BlockPos.containing(px, 0, pz);
			int surfaceY = heightCache.computeIfAbsent(col,
					p -> world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, p.getX(), p.getZ()));

			double py = surfaceY + PARTICLE_Y_BASE + rnd.nextDouble() * PARTICLE_Y_RAND;
			double fx = col.getX() + rnd.nextDouble();
			double fz = col.getZ() + rnd.nextDouble();

			server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, fx, py, fz, 1, 0, 0.05, 0, 0.01);
		}
	}

	private static void doGroundShockwave(LevelAccessor world, double x, double y, double z, Entity source) {
		// берем всех живых в кубе и фильтруем по кругу
		AABB box = AABB.ofSize(new Vec3(x, y, z), ATTACK_RADIUS*2, ATTACK_RADIUS*2, ATTACK_RADIUS*2);
		List<LivingEntity> victims = world.getEntitiesOfClass(LivingEntity.class, box, e -> isValidTarget(e, source));

		if (victims.isEmpty()) return;

		DamageSource ds = new DamageSource(world.holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, DS_ID)));
		// найдем ближайшего — по нему дадим силу тряски экрана
		LivingEntity closest = Collections.min(victims, Comparator.comparingDouble(e -> e.distanceToSqr(x, y, z)));
		double d2closest = closest.distanceToSqr(x, y, z);

		// сила шейка от расстояния
		if (d2closest <= R1_2) ScreenShakeUtil.startShake(20, 20.0F);
		else if (d2closest <= R2_2) ScreenShakeUtil.startShake(20, 15.0F);
		else if (d2closest <= R3_2) ScreenShakeUtil.startShake(20, 10.0F);
		else if (d2closest <= R4_2) ScreenShakeUtil.startShake(20, 8.0F);

		for (LivingEntity le : victims) {
			if (le.level().isClientSide()) continue;

			double d2 = le.distanceToSqr(x, y, z);

			if (d2 <= R1_2) {
				applyHit(le, ds, DMG_R1, SLOW_R1_T, SLOW_R1_A, KNOCK_Y_STRONG);
			} else if (d2 <= R2_2) {
				applyHit(le, ds, DMG_R2, SLOW_R2_T, SLOW_R2_A, KNOCK_Y_MED);
			} else if (d2 <= R3_2) {
				applyHit(le, ds, DMG_R3, SLOW_R3_T, SLOW_R3_A, KNOCK_Y_LIGHT);
			} else if (d2 <= R4_2) {
				applyHit(le, ds, DMG_R4, SLOW_R4_T, SLOW_R4_A, KNOCK_Y_TINY);
			}
		}
	}

	private static boolean isValidTarget(LivingEntity e, Entity source) {
		if (e == source) return false;                           // не бьём себя
		if (e instanceof DarkironkinEntity) return false;        // не бьём «себя» других типов
		if (e instanceof WitherSkeleton) return false;           // исключение: визер-скелеты
		// все остальные (игроки и мобы) — валидные цели
		return true;
	}

	private static void applyHit(LivingEntity target, DamageSource ds, float damage,
								 int slowTicks, int slowAmplifier, double knockY) {
		target.hurt(ds, damage);
		target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, slowTicks, slowAmplifier, true, false));
		target.push(0, knockY, 0);
	}

	// ===================== Summon =====================

	private static boolean canSummon(Entity e) {
		if (!(e instanceof DarkironkinEntity d)) return false;
		if (d.getEntityData().get(DarkironkinEntity.DATA_IsSmAttack)) return false;
		if (d.getEntityData().get(DarkironkinEntity.DATA_IsGrAttack)) return false;
		if (d.getEntityData().get(DarkironkinEntity.DATA_SmSwitcher)) return false;
		return (e instanceof LivingEntity le) && le.getHealth() <= 40f;
	}

	private static void startSummon(Entity e) {
		DarkironkinEntity d = (DarkironkinEntity) e;
		d.setAnimation("summon");
		d.getEntityData().set(DarkironkinEntity.DATA_IsSmAttack, true);
		d.getEntityData().set(DarkironkinEntity.DATA_SmSwitchTimer, SM_SWITCH_TICKS);
	}

	private static void spawnWitherSkeletonsAround(LivingEntity caster, int radius, int count, int maxAttempts) {
		Level lvl = caster.level();
		if (lvl.isClientSide) return;
		RandomSource rnd = RandomSource.create();

		for (int i = 0; i < count; i++) {
			int attempts = 0;
			boolean spawned = false;

			while (attempts++ < maxAttempts && !spawned) {
				double angle = rnd.nextDouble() * Math.PI * 2;
				double dist  = rnd.nextDouble() * radius;
				double xd = caster.getX() + Math.cos(angle) * dist;
				double zd = caster.getZ() + Math.sin(angle) * dist;

				for (int yd = lvl.getMaxBuildHeight(); yd >= lvl.getMinBuildHeight(); yd--) {
					BlockPos pos = new BlockPos((int) xd, yd, (int) zd);
					BlockState below = lvl.getBlockState(pos.below());
					if (!below.isSolid()) continue;

					WitherSkeleton sk = new WitherSkeleton(EntityType.WITHER_SKELETON, lvl);
					sk.setPos(xd + 0.5, yd, zd + 0.5);
					if (lvl.noCollision(sk)) {
						lvl.addFreshEntity(sk);
						if (lvl instanceof ServerLevel sl) {
							sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, xd + 0.5, yd + 1, zd + 0.5, 200, 0.5, 1, 0.5, 0.01);
						}
						spawned = true;
						break;
					}
				}
			}
		}
	}

	// ===================== Utils =====================

	private static void playSound(LevelAccessor world, double x, double y, double z, ResourceLocation id, SoundSource src) {
		if (!(world instanceof Level lvl)) return;
		if (!lvl.isClientSide()) {
			lvl.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(id), src, 1f, 1f);
		} else {
			lvl.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(id), src, 1f, 1f, false);
		}
	}
}
