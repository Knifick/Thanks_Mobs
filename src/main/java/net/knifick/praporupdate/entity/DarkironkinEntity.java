
package net.knifick.praporupdate.entity;

import net.knifick.praporupdate.init.PraporModEntities;
import net.knifick.praporupdate.item.GuideBookItem;
import net.knifick.praporupdate.procedures.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoEntity;

import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public class DarkironkinEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(DarkironkinEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(DarkironkinEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(DarkironkinEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_IsGrAttack = SynchedEntityData.defineId(DarkironkinEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_GrTimer = SynchedEntityData.defineId(DarkironkinEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_IsSmAttack = SynchedEntityData.defineId(DarkironkinEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> DATA_SmSwitcher = SynchedEntityData.defineId(DarkironkinEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_GrSwitchTimer = SynchedEntityData.defineId(DarkironkinEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_SmSwitchTimer = SynchedEntityData.defineId(DarkironkinEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public DarkironkinEntity(EntityType<DarkironkinEntity> type, Level world) {
		super(type, world);
		xpReward = 50;
		setNoAi(false);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SHOOT, false);
		builder.define(ANIMATION, "undefined");
		builder.define(TEXTURE, "dark_ironkin");
		builder.define(DATA_IsGrAttack, false);
		builder.define(DATA_GrTimer, 0);
		builder.define(DATA_IsSmAttack, false);
		builder.define(DATA_SmSwitcher, false);
		builder.define(DATA_GrSwitchTimer, 78);
		builder.define(DATA_SmSwitchTimer, 84);
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData livingdata) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
		BrolemOnSpawnProcedure.execute(this);
		return retval;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, false, false) {
			@Override
			public boolean canUse() {
				double x = DarkironkinEntity.this.getX();
				double y = DarkironkinEntity.this.getY();
				double z = DarkironkinEntity.this.getZ();
				Entity entity = DarkironkinEntity.this;
				Level world = DarkironkinEntity.this.level();
				return super.canUse() && IronkinReturnerProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = DarkironkinEntity.this.getX();
				double y = DarkironkinEntity.this.getY();
				double z = DarkironkinEntity.this.getZ();
				Entity entity = DarkironkinEntity.this;
				Level world = DarkironkinEntity.this.level();
				return super.canContinueToUse() && IronkinReturnerProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected boolean canPerformAttack(LivingEntity entity) {
				return this.isTimeToAttack() && this.mob.distanceToSqr(entity) < (this.mob.getBbWidth() * this.mob.getBbWidth() * 3 + entity.getBbWidth()) && this.mob.getSensing().hasLineOfSight(entity);
			}

			@Override
			public boolean canUse() {
				double x = DarkironkinEntity.this.getX();
				double y = DarkironkinEntity.this.getY();
				double z = DarkironkinEntity.this.getZ();
				Entity entity = DarkironkinEntity.this;
				Level world = DarkironkinEntity.this.level();
				return super.canUse() && IronkinReturnerProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = DarkironkinEntity.this.getX();
				double y = DarkironkinEntity.this.getY();
				double z = DarkironkinEntity.this.getZ();
				Entity entity = DarkironkinEntity.this;
				Level world = DarkironkinEntity.this.level();
				return super.canContinueToUse() && IronkinReturnerProcedure.execute(entity);
			}

		});
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this) {
			@Override
			public boolean canUse() {
				double x = DarkironkinEntity.this.getX();
				double y = DarkironkinEntity.this.getY();
				double z = DarkironkinEntity.this.getZ();
				Entity entity = DarkironkinEntity.this;
				Level world = DarkironkinEntity.this.level();
				return super.canUse() && IronkinReturnerProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = DarkironkinEntity.this.getX();
				double y = DarkironkinEntity.this.getY();
				double z = DarkironkinEntity.this.getZ();
				Entity entity = DarkironkinEntity.this;
				Level world = DarkironkinEntity.this.level();
				return super.canContinueToUse() && IronkinReturnerProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8) {
			@Override
			public boolean canUse() {
				double x = DarkironkinEntity.this.getX();
				double y = DarkironkinEntity.this.getY();
				double z = DarkironkinEntity.this.getZ();
				Entity entity = DarkironkinEntity.this;
				Level world = DarkironkinEntity.this.level();
				return super.canUse() && IronkinReturnerProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = DarkironkinEntity.this.getX();
				double y = DarkironkinEntity.this.getY();
				double z = DarkironkinEntity.this.getZ();
				Entity entity = DarkironkinEntity.this;
				Level world = DarkironkinEntity.this.level();
				return super.canContinueToUse() && IronkinReturnerProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				double x = DarkironkinEntity.this.getX();
				double y = DarkironkinEntity.this.getY();
				double z = DarkironkinEntity.this.getZ();
				Entity entity = DarkironkinEntity.this;
				Level world = DarkironkinEntity.this.level();
				return super.canUse() && IronkinReturnerProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = DarkironkinEntity.this.getX();
				double y = DarkironkinEntity.this.getY();
				double z = DarkironkinEntity.this.getZ();
				Entity entity = DarkironkinEntity.this;
				Level world = DarkironkinEntity.this.level();
				return super.canContinueToUse() && IronkinReturnerProcedure.execute(entity);
			}
		});
	}

	@Override
	public SoundEvent getAmbientSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:idle_iron")).get().value();
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:golem_steps")).get().value(), 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.blaze.hurt")).get().value();
	}

	@Override
	public SoundEvent getDeathSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:golem_death")).get().value();
	}

	@Override
	public void actuallyHurt(ServerLevel serverLevel, DamageSource source, float amount) {
		DarkironkinOnRMCProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
		if (source.is(DamageTypes.IN_FIRE))
			return;
		if (source.getDirectEntity() instanceof AbstractArrow)
			return;
		if (source.is(DamageTypes.FALL))
			return;
		super.actuallyHurt(serverLevel, source, amount);
	}

	@Override
	public boolean fireImmune() {
		return true;
	}

	@Override
	public void addAdditionalSaveData(ValueOutput compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("DataIsGrAttack", this.entityData.get(DATA_IsGrAttack));
		compound.putInt("DataGrTimer", this.entityData.get(DATA_GrTimer));
		compound.putBoolean("DataIsSmAttack", this.entityData.get(DATA_IsSmAttack));
		compound.putBoolean("DataSmSwitcher", this.entityData.get(DATA_SmSwitcher));
		compound.putInt("DataGrSwitchTimer", this.entityData.get(DATA_GrSwitchTimer));
		compound.putInt("DataSmSwitchTimer", this.entityData.get(DATA_SmSwitchTimer));
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);

		this.setTexture(input.getStringOr("Texture", this.getTexture()));
		this.entityData.set(DATA_IsGrAttack, input.getBooleanOr("DataIsGrAttack", this.entityData.get(DATA_IsGrAttack)));
		this.entityData.set(DATA_GrTimer, input.getIntOr("DataGrTimer", this.entityData.get(DATA_GrTimer)));
		this.entityData.set(DATA_IsSmAttack, input.getBooleanOr("DataIsSmAttack", this.entityData.get(DATA_IsSmAttack)));
		this.entityData.set(DATA_SmSwitcher, input.getBooleanOr("DataSmSwitcher", this.entityData.get(DATA_SmSwitcher)));
		this.entityData.set(DATA_GrSwitchTimer, input.getIntOr("DataGrSwitchTimer", this.entityData.get(DATA_GrSwitchTimer)));
		this.entityData.set(DATA_SmSwitchTimer, input.getIntOr("DataSmSwitchTimer", this.entityData.get(DATA_SmSwitchTimer)));
	}


	@Override
	public void baseTick() {
		super.baseTick();
		DarkironkinPerTickProcedure.execute(this);
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose pose) {
		return super.getDefaultDimensions(pose).scale(1f);
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
		event.register(PraporModEntities.DARKIRONKIN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			return SoulSpawnConditionProcedure.execute(world);
		}, RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 200);
		builder = builder.add(Attributes.ARMOR, 3);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 12);
		builder = builder.add(Attributes.FOLLOW_RANGE, 48);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 3);
		return builder;
	}

	private PlayState movementPredicate(AnimationTest<DarkironkinEntity> event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() && this.onGround() && !this.isAggressive() && !this.isSprinting())) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
			}
			if (this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("run"));
			}
			if (!this.onGround()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("fall"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("run"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationTest<DarkironkinEntity> event) {
		double d1 = this.getX() - this.xOld;
		double d0 = this.getZ() - this.zOld;
		float velocity = (float) Math.sqrt(d1 * d1 + d0 * d0);
		if (getAttackAnim(event.getData(DataTickets.PARTIAL_TICK)) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 7L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.controller().getAnimationState() == AnimationController.State.STOPPED) {
			event.controller().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("hit"));
		}
		return PlayState.CONTINUE;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationTest<DarkironkinEntity> event) {
		if (!animationprocedure.equals("empty") && event.controller().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.controller().forceAnimationReset();
			event.controller().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.controller().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.controller().forceAnimationReset();
			}
		} else if (animationprocedure.equals("empty")) {
			prevAnim = "empty";
			return PlayState.STOP;
		}
		prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}

	@Override
	public void die(DamageSource source) {
		if(source.getEntity() instanceof Player player)
			GuideBookItem.addToBook(player, this, 2);
		super.die(source);
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 104) {
			this.remove(DarkironkinEntity.RemovalReason.KILLED);
		}
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>("movement", 4, this::movementPredicate));
		data.add(new AnimationController<>("attacking", 4, this::attackingPredicate));
		data.add(new AnimationController<>("procedure", 4, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}