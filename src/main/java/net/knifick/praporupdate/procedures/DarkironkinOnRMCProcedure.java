package net.knifick.praporupdate.procedures;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.knifick.praporupdate.entity.DarkironkinEntity;
import net.knifick.praporupdate.util.ironkin.ScreenShakeUtil;
import net.knifick.praporupdate.PraporMod;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DarkironkinOnRMCProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity player = null;
		if (entity instanceof Mob _mobEnt0 && _mobEnt0.isAggressive() && !(entity instanceof DarkironkinEntity _datEntL1 && _datEntL1.getEntityData().get(DarkironkinEntity.DATA_IsGrAttack))
				&& !(entity instanceof DarkironkinEntity _datEntL2 && _datEntL2.getEntityData().get(DarkironkinEntity.DATA_IsSmAttack))
				&& (entity instanceof DarkironkinEntity _datEntI ? _datEntI.getEntityData().get(DarkironkinEntity.DATA_GrTimer) : 0) == 0) {
			if (entity instanceof DarkironkinEntity _datEntSetI)
				_datEntSetI.getEntityData().set(DarkironkinEntity.DATA_GrTimer, 100);
			if (entity instanceof DarkironkinEntity _datEntSetL)
				_datEntSetL.getEntityData().set(DarkironkinEntity.DATA_IsGrAttack, true);
			if (entity instanceof DarkironkinEntity _datEntSetI)
				_datEntSetI.getEntityData().set(DarkironkinEntity.DATA_GrSwitchTimer, 78);
			if (entity instanceof DarkironkinEntity) {
				((DarkironkinEntity) entity).setAnimation("groundHit");
			}
			PraporMod.queueServerWork(27, () -> {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:ironkin_ground_hit")), SoundSource.HOSTILE, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:ironkin_ground_hit")), SoundSource.HOSTILE, 1, 1, false);
					}
				}
				// ЗАМЕНА СОЗДАНИЯ ЧАСТИЦ
				if (world instanceof ServerLevel) {
					ServerLevel serverLevel = (ServerLevel) world;
					int particleCount = 500; // Общее количество частиц
					double radius = 15.0; // Радиус области

					// Кэш для хранения высот блоков
					Map<BlockPos, Integer> heightCache = new HashMap<>();
					RandomSource random = RandomSource.create();

					for (int i = 0; i < particleCount; i++) {
						// Случайный угол и расстояние
						double angle = random.nextDouble() * Math.PI * 2;
						double distance = random.nextDouble() * radius;

						// Случайная позиция в круге
						double px = x + Mth.cos((float) angle) * distance;
						double pz = z + Mth.sin((float) angle) * distance;

						// Определение позиции блока
						BlockPos blockPos = BlockPos.containing(px, 0, pz);

						// Получение высоты из кэша или вычисление
						int surfaceY = heightCache.computeIfAbsent(blockPos,
								pos -> world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()));

						// Случайное смещение в пределах блока
						double py = surfaceY + 0.1 + random.nextDouble() * 0.15;
						double pxFinal = blockPos.getX() + random.nextDouble();
						double pzFinal = blockPos.getZ() + random.nextDouble();

						// Создание частицы с движением вверх
						serverLevel.sendParticles(
								ParticleTypes.CAMPFIRE_COSY_SMOKE,
								pxFinal,
								py,
								pzFinal,
								1, // Количество частиц
								0, // X-разброс
								0.05, // Y-скорость (движение вверх)
								0, // Z-разброс
								0.01 // Скорость
						);
					}
				}
				if (!(((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 3, 3, 3), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null)) == null)) {
					ScreenShakeUtil.startShake(20, 20.0F);
					((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).hurt(new DamageSource(world.holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("prapor:darkironkinhurt")))), 20);
					if (((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 20, 20, 20), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)) instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 2, true, false));
					((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).push(0, 0.7, 0);
				} else if (!(((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 5, 5, 5), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null)) == null)) {
					ScreenShakeUtil.startShake(20, 15.0F);
					((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).hurt(new DamageSource(world.holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("prapor:darkironkinhurt")))), 18);
					if (((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 20, 20, 20), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)) instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2, true, false));
					((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).push(0, 0.6, 0);
				} else if (!(((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 7, 7, 7), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null)) == null)) {
					ScreenShakeUtil.startShake(20, 10.0F);
					((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).hurt(new DamageSource(world.holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("prapor:darkironkinhurt")))), 16);
					if (((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 20, 20, 20), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)) instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 150, 1, true, false));
					((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).push(0, 0.6, 0);
				} else if (!(((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 10, 10, 10), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null)) == null)) {
					ScreenShakeUtil.startShake(20, 10.0F);
					((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).hurt(new DamageSource(world.holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("prapor:darkironkinhurt")))), 14);
					if (((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 20, 20, 20), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)) instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 150, 1, true, false));
					((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).push(0, 0.4, 0);
				} else if (!(((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 13, 13, 13), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null)) == null)) {
					ScreenShakeUtil.startShake(20, 8.0F);
					((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).hurt(new DamageSource(world.holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("prapor:darkironkinhurt")))), 6);
					if (((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 20, 20, 20), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)) instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 150, 1, true, false));
					((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 15, 15, 15), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).push(0, 0.4, 0);
				} else if (!(((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 16, 16, 16), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null)) == null)) {
					ScreenShakeUtil.startShake(20, 8.0F);
					if (((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 20, 20, 20), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)) instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1, true, false));
					((Entity) world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 20, 20, 20), e -> true).stream().sorted(new Object() {
						Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
							return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
						}
					}.compareDistOf(x, y, z)).findFirst().orElse(null)).push(0, 0.2, 0);
				}
			});
		} else if (entity instanceof Mob _mobEnt62 && _mobEnt62.isAggressive() && !(entity instanceof DarkironkinEntity _datEntL63 && _datEntL63.getEntityData().get(DarkironkinEntity.DATA_IsSmAttack))
				&& !(entity instanceof DarkironkinEntity _datEntL64 && _datEntL64.getEntityData().get(DarkironkinEntity.DATA_IsGrAttack))
				&& !(entity instanceof DarkironkinEntity _datEntL65 && _datEntL65.getEntityData().get(DarkironkinEntity.DATA_SmSwitcher)) && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) <= 40) {
			if (entity instanceof DarkironkinEntity) {
				((DarkironkinEntity) entity).setAnimation("summon");
			}
			if (entity instanceof DarkironkinEntity _datEntSetL)
				_datEntSetL.getEntityData().set(DarkironkinEntity.DATA_IsSmAttack, true);
			if (entity instanceof DarkironkinEntity _datEntSetI)
				_datEntSetI.getEntityData().set(DarkironkinEntity.DATA_SmSwitchTimer, 84);
			PraporMod.queueServerWork(42, () -> {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("event.raid.horn")), SoundSource.NEUTRAL, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("event.raid.horn")), SoundSource.NEUTRAL, 1, 1, false);
					}
				}
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:summon")), SoundSource.NEUTRAL, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:summon")), SoundSource.NEUTRAL, 1, 1, false);
					}
				}
				Level _world = entity.level();
				if (_world.isClientSide)
					return;
				RandomSource random = RandomSource.create();
				final int radius = 8;
				final int count = 5;
				final int maxAttempts = 15;
				for (int i = 0; i < count; i++) {
					int attempts = 0;
					boolean spawned = false;
					while (attempts < maxAttempts && !spawned) {
						// Генерация случайной позиции в радиусе
						double angle = random.nextDouble() * Math.PI * 2;
						double distance = random.nextDouble() * radius;
						double xd = entity.getX() + Math.cos(angle) * distance;
						double zd = entity.getZ() + Math.sin(angle) * distance;
						// Поиск подходящего Y
						for (int yd = _world.getMaxBuildHeight(); yd >= _world.getMinBuildHeight(); yd--) {
							BlockPos pos = new BlockPos((int) xd, yd, (int) zd);
							BlockState blockBelow = _world.getBlockState(pos.below());
							if (blockBelow.isSolid()) {
								WitherSkeleton skeleton = new WitherSkeleton(EntityType.WITHER_SKELETON, _world);
								skeleton.setPos(xd + 0.5, yd, zd + 0.5);
								// Проверка на отсутствие коллизий
								if (_world.noCollision(skeleton)) {
									_world.addFreshEntity(skeleton);
									if (_world instanceof ServerLevel _level)
										_level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, xd + 0.5, (yd + 1), zd + 0.5, 200, 0.5, 1, 0.5, 0.01);
									spawned = true;
									break;
								}
							}
						}
						attempts++;
					}
				}
			});
		}
	}
}
