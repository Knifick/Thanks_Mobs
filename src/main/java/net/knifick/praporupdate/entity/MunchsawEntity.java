package net.knifick.praporupdate.entity;

import net.knifick.praporupdate.init.PraporModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MunchsawEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(MunchsawEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(MunchsawEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(MunchsawEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> SAW = SynchedEntityData.defineId(MunchsawEntity.class, EntityDataSerializers.BOOLEAN);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public Vec3 playerPos;
	public String animationprocedure = "empty";

	public MunchsawEntity(EntityType<MunchsawEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SHOOT, false);
		builder.define(ANIMATION, "undefined");
		builder.define(TEXTURE, "munchsaw");
		builder.define(SAW, false);
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	public boolean isSaw() {
		return this.entityData.get(SAW);
	}

	public void setSaw(boolean is) {
		this.entityData.set(SAW, is);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		/*this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.8D, true) {
			private final double straightSpeed = 0.8D;

			private boolean followingPath = false;
			private Vec3 pathDestination = null;
			private final double finishDistance = 1.0D;
			private int pathAttemptTicks = 0;
			private final int maxPathAttemptTicks = 200; // таймаут (в тиках) на попытки проложить путь

			@Override
			public boolean canUse() {
				LivingEntity target = this.mob.getTarget();
				return target != null && target.isAlive();
			}

			@Override
			public boolean canContinueToUse() {
				LivingEntity target = this.mob.getTarget();
				return target != null && target.isAlive();
			}

			@Override
			public void start() {
				this.mob.setAggressive(true);
				followingPath = false;
				pathDestination = null;
				pathAttemptTicks = 0;
				LivingEntity t = this.mob.getTarget();
				if (t != null && this.mob.getNavigation() != null) {
					this.mob.getNavigation().moveTo(t, this.straightSpeed);
				}
			}

			@Override
			public void stop() {
				LivingEntity target = this.mob.getTarget();
				if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
					this.mob.setTarget(null);
				}
				this.mob.setAggressive(false);
				if (this.mob.getNavigation() != null) this.mob.getNavigation().stop();
				followingPath = false;
				pathDestination = null;
				pathAttemptTicks = 0;
			}

			private void resumeChase(LivingEntity target) {
				// выключаем режим "следования по запомненной точке" и сразу возобновляем преследование игрока
				followingPath = false;
				pathDestination = null;
				pathAttemptTicks = 0;
				PathNavigation nav = this.mob.getNavigation();
				if (nav != null) nav.stop(); // явно освобождаем nav, чтобы MoveControl снова контролировал движение
				// немедленно даём команду на движение к текущей позиции цели (горизонтально)
				this.mob.getMoveControl().setWantedPosition(target.getX(), this.mob.getY(), target.getZ(), this.straightSpeed);
			}

			@Override
			public void tick() {
				LivingEntity target = this.mob.getTarget();
				if (target == null) return;

				this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
				PathNavigation nav = this.mob.getNavigation();

				// Если столкнулись и ещё не начали "следовать по пути" — переключаемся
				if (this.mob.horizontalCollision && !followingPath) {
					followingPath = true;
					pathDestination = new Vec3(target.getX(), target.getY(), target.getZ());
					pathAttemptTicks = 0;
					if (nav != null) {
						nav.moveTo(target, this.straightSpeed);
					}
				}

				if (followingPath) {
					pathAttemptTicks++;

					if (nav != null) {
						// Навигация управляет движением, пока есть активный путь
						if (nav.isDone()) {
							// nav завершил прокладку (либо дошёл до цели, либо не нашёл путь)
							double distToSaved = this.mob.position().distanceTo(pathDestination);
							if (distToSaved <= finishDistance) {
								// Дошли до сохранённой точки -> возобновляем обычное преследование
								resumeChase(target);
							} else {
								// Если путь не найден или nav завершился, попробуем ещё раз
								if (pathAttemptTicks < maxPathAttemptTicks) {
									// попробуем снова проложить к текущей позиции цели
									nav.moveTo(target, this.straightSpeed);
								} else {
									// таймаут — считаем, что путь не найти, переключаемся в ручной режим преследования
									resumeChase(target);
								}
							}
						} else {
							// nav ещё строит/следует по пути — ничего не делаем, ждем
							// можно добавить дополнительные проверки: stuck detector, stuckCounter и т.д.
						}
					} else {
						// fallback: если навигации нет — вручную толкаем моба к сохранённой точке
						if (pathDestination != null) {
							Vec3 dir = new Vec3(pathDestination.x - this.mob.getX(), 0, pathDestination.z - this.mob.getZ());
							double len = Math.sqrt(dir.x * dir.x + dir.z * dir.z);
							if (len > 1e-6) {
								Vec3 push = dir.scale(0.35D / len);
								this.mob.setDeltaMovement(push.x, 0.08D, push.z);
							}
							if (this.mob.position().distanceTo(pathDestination) <= finishDistance) {
								resumeChase(target);
							}
						} else {
							// нет destination — сбрасываем
							resumeChase(target);
						}
					}

				} else {
					// Обычный режим: ручное горизонтальное движение
					if (nav != null && !nav.isDone()) {
						// если navigation всё ещё двигается по старому пути — остановим его,
						// чтобы MoveControl снова управлял движением
						nav.stop();
					}
					this.mob.getMoveControl().setWantedPosition(
							target.getX(),
							this.mob.getY(),
							target.getZ(),
							this.straightSpeed
					);
				}

				if (this.canPerformAttack(target)) {
					this.checkAndPerformAttack(target);
				}
			}

			@Override
			protected boolean canPerformAttack(LivingEntity target) {
				if (this.mob.position().distanceTo(target.position()) < 1.3 && this.mob.tickCount % 20 == 0) {
                    this.mob.getNavigation();
                    this.mob.getNavigation().stop();
					return true;
				}
				return false;
			}
		});*/
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.5));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(5, new SmoothSwimRandomGoal(this));
		//this.goalSelector.addGoal(5, new FloatGoal(this));
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float f) {
		return super.getPassengerAttachmentPoint(entity, dimensions, f).add(0, -0.1f, 0);
	}

	@Override
	public SoundEvent getAmbientSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:sucker_idle")).get().value();
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:sucker_hurt")).get().value();
	}

	@Override
	public SoundEvent getDeathSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:sucker_hurt")).get().value();
	}

	@Override
	protected void actuallyHurt(ServerLevel serverLevel, DamageSource source, float amount) {
		if (source.is(DamageTypes.FALL))
			return;
		super.actuallyHurt(serverLevel, source, amount);
	}

	@Override
	protected PathNavigation createNavigation(Level world) {
		AmphibiousPathNavigation nav = new AmphibiousPathNavigation(this, world);
		nav.setCanFloat(true); // позволяет поверхностно «плавать»/всплывать
		return nav;
	}

//	@Override
//	public void travel(Vec3 vec) {
//		if (this.isInWater()) {
//			// простая, проверенная логика движения в воде
//			this.moveRelative(0.02F, vec);              // translate input to motion
//			this.move(MoverType.SELF, this.getDeltaMovement());
//			this.setDeltaMovement(this.getDeltaMovement().scale(0.9D)); // водное трение
//			return;
//		}
//		super.travel(vec);
//	}

	@Override
	public void addAdditionalSaveData(ValueOutput compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("saw", this.entityData.get(SAW));
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);

		// если ключа нет, оставляем текущее значение
		this.setTexture(input.getStringOr("Texture", this.getTexture()));
		this.entityData.set(SAW, input.getBooleanOr("saw", this.entityData.get(SAW)));
	}


	@Override
	public EntityDimensions getDefaultDimensions(Pose pose) {
		return super.getDefaultDimensions(pose).scale(1f);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.isInWater()) {
			this.setAirSupply(300); // 300 — стандартный максимальный запас воздуха (5 секунд * 20 тиков)
		}
		this.updateSwingTime();
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
		event.register(PraporModEntities.PRAPOR.get(),
				SpawnPlacementTypes.IN_WATER,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) ->
						world.getFluidState(pos).is(Fluids.WATER)            // в воде
								&& world.getRawBrightness(pos, 0) <= 8              // тёмно (пещера)
				, RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.45);
		builder = builder.add(Attributes.MAX_HEALTH, 20);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.STEP_HEIGHT, 1.1);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
		return builder;
	}

	private PlayState movementPredicate(AnimationTest<MunchsawEntity> state) {
		// Если моб в воде
		if (this.isInWater()) {
			return state.setAndContinue(RawAnimation.begin().thenLoop("swim"));
		}

		if (this.isSprinting() || (this.isAggressive() && state.isMoving())) {
			return state.setAndContinue(RawAnimation.begin().thenLoop("run"));
		}
		if ((state.isMoving())
				&& this.onGround() && !this.isAggressive() && !this.isSprinting()) {
			return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
		}
		return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
	}


	private PlayState attackPredicate(AnimationTest<MunchsawEntity> state) {
		if (this.swinging) {
			state.controller().forceAnimationReset();
			return state.setAndContinue(RawAnimation.begin().thenPlay("attack"));
		}
		return PlayState.STOP;
	}

//	private <E extends GeoAnimatable> PlayState suckPredicate(AnimationState<E> state) {
//		if (isSucking()) {
//			return state.setAndContinue(RawAnimation.begin().thenLoop("suck"));
//		}
//		return PlayState.STOP;
//	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.triggerAnim("triggers", animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>("move",   5, this::movementPredicate));
		controllers.add(new AnimationController<>("attack", 0, this::attackPredicate));
		//controllers.add(new AnimationController<>(this, "suck",   5, this::suckPredicate));

		controllers.add(new AnimationController<>("triggers", 0, s -> PlayState.STOP)
				.triggerableAnim("dance", RawAnimation.begin().thenPlay("dance")));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	class SmoothSwimRandomGoal extends Goal {
		private final MunchsawEntity mob;
		private Vec3 targetPos;        // куда плыть
		private int cooldown;          // сколько тиков ещё плыть

		public SmoothSwimRandomGoal(MunchsawEntity mob) {
			this.mob = mob;
		}

		@Override
		public boolean canUse() {
			return mob.isInWater();
		}

		@Override
		public void start() {
			pickNewTarget();
		}

		@Override
		public void tick() {
			if (targetPos == null || cooldown-- <= 0 || mob.position().distanceTo(targetPos) < 1.0) {
				pickNewTarget();
			}

			if (targetPos != null) {
				if(this.mob.level() instanceof ServerLevel level){
					level.sendParticles(ParticleTypes.FLAME,
							targetPos.x, targetPos.y, targetPos.z,
							1,0,0,0,
							0);
				}
				Vec3 dir = targetPos.subtract(mob.position()).normalize().scale(0.05); // скорость
				Vec3 newMotion = mob.getDeltaMovement().add(dir).scale(0.9);           // сглаживание
				mob.setDeltaMovement(newMotion);
				Vec3 motion = mob.getDeltaMovement();
				if (motion.lengthSqr() > 0.001) {
					mob.setYRot((float)(Mth.atan2(motion.z, motion.x) * (180F / Math.PI)) - 90F);
					mob.yBodyRot = mob.getYRot();   // тело
					mob.yHeadRot = mob.getYRot();   // голова
				}
			} else {
				// если цели нет, просто держим на месте и не даём тонуть
				mob.setDeltaMovement(mob.getDeltaMovement().multiply(0.8, 0.0, 0.8));
			}
		}

		private void pickNewTarget() {
			RandomSource rnd = mob.getRandom();
			// радиус поиска
			double dx = rnd.nextDouble() * 8 - 4;
			double dy = rnd.nextDouble() * 4 - 2; // ограничим по вертикали
			double dz = rnd.nextDouble() * 8 - 4;

			Vec3 pos = new Vec3(mob.getX() + dx, mob.getY() + dy, mob.getZ() + dz);
			if (mob.level().getFluidState(mob.blockPosition()).is(Fluids.WATER)
			&& mob.level().getFluidState(new BlockPos((int) pos.x, (int) pos.y, (int) pos.z)).is(Fluids.WATER)) {
				targetPos = pos;
				cooldown = 100 + rnd.nextInt(100); // сколько тиков держать цель
			} else {
				targetPos = null;
			}
		}
	}
}
