
package net.knifick.praporupdate.entity;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Vindicator;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoEntity;

import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;

import net.knifick.praporupdate.procedures.UsualAIProcedure;
import net.knifick.praporupdate.procedures.UnusualAIProcedure;
import net.knifick.praporupdate.procedures.BastardRCMProcedure;
import net.knifick.praporupdate.procedures.BastardPerTickProcedure;
import net.knifick.praporupdate.init.PraporModEntities;

import java.util.function.Predicate;

public class BastardEntity extends Raider implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(BastardEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(BastardEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BastardEntity.class, EntityDataSerializers.STRING);
	public static final EnumProxy<Raid.RaiderType> RAIDER_TYPE = new EnumProxy<>(Raid.RaiderType.class, PraporModEntities.BASTARD, new int[]{0, 0, 0, 0, 0, 1, 1, 2});
	public static final EntityDataAccessor<Integer> DATA_State = SynchedEntityData.defineId(BastardEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_ex = SynchedEntityData.defineId(BastardEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_ey = SynchedEntityData.defineId(BastardEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_ez = SynchedEntityData.defineId(BastardEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public BastardEntity(EntityType<BastardEntity> type, Level world) {
		super(type, world);
		xpReward = 1;
		setNoAi(false);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SHOOT, false);
		builder.define(ANIMATION, "undefined");
		builder.define(TEXTURE, "padla");
		builder.define(DATA_State, 0);
		builder.define(DATA_ex, 0);
		builder.define(DATA_ey, 0);
		builder.define(DATA_ez, 0);
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	private Predicate<Difficulty> createDifficultyPredicate() {
		return difficulty -> difficulty == Difficulty.HARD;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new BreakDoorGoal(this, createDifficultyPredicate()));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, (float) 15) {
			@Override
			public boolean canUse() {
				double x = BastardEntity.this.getX();
				double y = BastardEntity.this.getY();
				double z = BastardEntity.this.getZ();
				Entity entity = BastardEntity.this;
				Level world = BastardEntity.this.level();
				return super.canUse() && UnusualAIProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = BastardEntity.this.getX();
				double y = BastardEntity.this.getY();
				double z = BastardEntity.this.getZ();
				Entity entity = BastardEntity.this;
				Level world = BastardEntity.this.level();
				return super.canContinueToUse() && UnusualAIProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(3, new OpenDoorGoal(this, true));
		this.goalSelector.addGoal(4, new MoveBackToVillageGoal(this, 0.6, false));
		this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				double x = BastardEntity.this.getX();
				double y = BastardEntity.this.getY();
				double z = BastardEntity.this.getZ();
				Entity entity = BastardEntity.this;
				Level world = BastardEntity.this.level();
				return super.canUse() && UsualAIProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = BastardEntity.this.getX();
				double y = BastardEntity.this.getY();
				double z = BastardEntity.this.getZ();
				Entity entity = BastardEntity.this;
				Level world = BastardEntity.this.level();
				return super.canContinueToUse() && UsualAIProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(6, new FloatGoal(this));
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:bastard_idle"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:bastard_hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:bastard_death"));
	}

	@Override
	public SoundEvent getCelebrateSound() {
		return SoundEvents.EMPTY;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("DataState", this.entityData.get(DATA_State));
		compound.putInt("Dataex", this.entityData.get(DATA_ex));
		compound.putInt("Dataey", this.entityData.get(DATA_ey));
		compound.putInt("Dataez", this.entityData.get(DATA_ez));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("DataState"))
			this.entityData.set(DATA_State, compound.getInt("DataState"));
		if (compound.contains("Dataex"))
			this.entityData.set(DATA_ex, compound.getInt("Dataex"));
		if (compound.contains("Dataey"))
			this.entityData.set(DATA_ey, compound.getInt("Dataey"));
		if (compound.contains("Dataez"))
			this.entityData.set(DATA_ez, compound.getInt("Dataez"));
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		InteractionResult retval = InteractionResult.sidedSuccess(this.level().isClientSide());
		super.mobInteract(sourceentity, hand);
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity entity = this;
		Level world = this.level();

		BastardRCMProcedure.execute(world, entity, sourceentity);
		return retval;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		BastardPerTickProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose pose) {
		return super.getDefaultDimensions(pose).scale(1f);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
		event.register(PraporModEntities.BASTARD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getRawBrightness(pos, 0) > 8), RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	@Override
	public void applyRaidBuffs(ServerLevel serverLevel, int num, boolean logic) {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 20);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState event) {
		if (!animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.getController().forceAnimationReset();
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (animationprocedure.equals("empty")) {
			prevAnim = "empty";
			return PlayState.STOP;
		}
		prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.remove(BastardEntity.RemovalReason.KILLED);
			this.dropExperience(this);
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
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
