
package net.knifick.praporupdate.entity;

import net.knifick.praporupdate.client.screens.BookScreen;
import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.item.GuideBookItem;
import net.knifick.praporupdate.network.PraporModVariables;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
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
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:bastard_idle")).get().value();
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:bastard_hurt")).get().value();
	}

	@Override
	public SoundEvent getDeathSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:bastard_death")).get().value();
	}

	@Override
	public SoundEvent getCelebrateSound() {
		return SoundEvents.EMPTY;
	}

	@Override
	public void addAdditionalSaveData(ValueOutput compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putInt("DataState", this.entityData.get(DATA_State));
		compound.putInt("Dataex", this.entityData.get(DATA_ex));
		compound.putInt("Dataey", this.entityData.get(DATA_ey));
		compound.putInt("Dataez", this.entityData.get(DATA_ez));
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);

		input.getString("Texture")
				.ifPresent(this::setTexture);

		input.getInt("DataState")
				.ifPresent(v -> this.entityData.set(DATA_State, v));

		input.getInt("Dataex")
				.ifPresent(v -> this.entityData.set(DATA_ex, v));

		input.getInt("Dataey")
				.ifPresent(v -> this.entityData.set(DATA_ey, v));

		input.getInt("Dataez")
				.ifPresent(v -> this.entityData.set(DATA_ez, v));
	}


	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		InteractionResult retval;
		if(this.level().isClientSide())
			retval = InteractionResult.SUCCESS;
		else
			retval = InteractionResult.SUCCESS_SERVER;
		super.mobInteract(sourceentity, hand);
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity entity = this;
		Level world = this.level();
//		if(itemstack.is(PraporModItems.GUIDE_BOOK)){
//			GuideBookItem.addToBook(sourceentity, this, 1);
//		}

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

	private PlayState movementPredicate(AnimationTest<BastardEntity> test) {
		if ("empty".equals(this.animationprocedure)) {
			// аналог event.isMoving() и проверки амплитуды шага
			final boolean moving = Boolean.TRUE.equals(test.getDataOrDefault(DataTickets.IS_MOVING, false));

			if (moving) {
				return test.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			return test.setAndContinue(RawAnimation.begin().thenLoop("idle"));
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationTest<BastardEntity> test) {
		if (!"empty".equals(animationprocedure)
				&& (test.controller().getAnimationState() == AnimationController.State.STOPPED
				|| !this.animationprocedure.equals(prevAnim))) {

			if (!this.animationprocedure.equals(prevAnim)) {
				test.controller().forceAnimationReset();
			}

			test.controller().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));

			// Когда одноразовая анимация завершилась — сбрасываем «процедуру»
			if (test.controller().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				test.controller().forceAnimationReset();
			}
		} else if ("empty".equals(animationprocedure)) {
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
		data.add(new AnimationController<>("procedure", 4, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
