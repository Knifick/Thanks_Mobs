
package net.knifick.praporupdate.entity;

import net.knifick.praporupdate.client.screens.BookScreen;
import net.knifick.praporupdate.item.GuideBookItem;
import net.knifick.praporupdate.network.PraporModVariables;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
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
import net.neoforged.neoforge.event.EventHooks;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.BlockTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;

import net.knifick.praporupdate.procedures.PraporkaToPraporProcedure;
import net.knifick.praporupdate.procedures.PraporTamedProcedureProcedure;
import net.knifick.praporupdate.procedures.PraporSlowFallingProcedure;
import net.knifick.praporupdate.procedures.DanceRetirnerProcedure;
import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.init.PraporModEntities;

import java.util.List;
import java.util.Objects;

public class PraporEntity extends TamableAnimal implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(PraporEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(PraporEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(PraporEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_isTamed = SynchedEntityData.defineId(PraporEntity.class, EntityDataSerializers.BOOLEAN);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public PraporEntity(EntityType<PraporEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SHOOT, false);
		builder.define(ANIMATION, "undefined");
		builder.define(TEXTURE, "prapor");
		builder.define(DATA_isTamed, false);
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Chicken.class, (float) 6, 1.2, 1.2));
		this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1, (float) 10, (float) 2));
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected boolean canPerformAttack(LivingEntity entity) {
				return this.isTimeToAttack() && this.mob.distanceToSqr(entity) < (this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth() * entity.getBbWidth()) && this.mob.getSensing().hasLineOfSight(entity);
			}
		});
		this.targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
		this.goalSelector.addGoal(5, new TemptGoal(this, 1, Ingredient.of(Items.SAND), false));
		this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.8));
		this.goalSelector.addGoal(7, new OwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(8, new OwnerHurtTargetGoal(this) {
			@Override
			public boolean canUse() {
				double x = PraporEntity.this.getX();
				double y = PraporEntity.this.getY();
				double z = PraporEntity.this.getZ();
				Entity entity = PraporEntity.this;
				Level world = PraporEntity.this.level();
				return super.canUse() && PraporTamedProcedureProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = PraporEntity.this.getX();
				double y = PraporEntity.this.getY();
				double z = PraporEntity.this.getZ();
				Entity entity = PraporEntity.this;
				Level world = PraporEntity.this.level();
				return super.canContinueToUse() && PraporTamedProcedureProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(9, new FollowParentGoal(this, 0.8));
		this.goalSelector.addGoal(10, new BreedGoal(this, 1));
		this.goalSelector.addGoal(11, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				double x = PraporEntity.this.getX();
				double y = PraporEntity.this.getY();
				double z = PraporEntity.this.getZ();
				Entity entity = PraporEntity.this;
				Level world = PraporEntity.this.level();
				return super.canUse() && DanceRetirnerProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = PraporEntity.this.getX();
				double y = PraporEntity.this.getY();
				double z = PraporEntity.this.getZ();
				Entity entity = PraporEntity.this;
				Level world = PraporEntity.this.level();
				return super.canContinueToUse() && DanceRetirnerProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(12, new FloatGoal(this));
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float f) {
		return super.getPassengerAttachmentPoint(entity, dimensions, f).add(0, -0.1f, 0);
	}

	@Override
	public SoundEvent getAmbientSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:idle")).get().value();
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:hurt")).get().value();
	}

	@Override
	public SoundEvent getDeathSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:death")).get().value();
	}

	@Override
	protected void actuallyHurt(ServerLevel serverLevel, DamageSource damageSource, float amount) {
		if (damageSource.is(DamageTypes.FALL))
			return;
		super.actuallyHurt(serverLevel, damageSource, amount);
	}

	@Override
	public void addAdditionalSaveData(ValueOutput compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("DataisTamed", this.entityData.get(DATA_isTamed));
	}

	@Override
	public void readAdditionalSaveData(ValueInput compound) {
		super.readAdditionalSaveData(compound);
		compound.getString("Texture")
				.ifPresent(this::setTexture);
		this.entityData.set(DATA_isTamed, compound.getBooleanOr("DataisTamed", this.entityData.get(DATA_isTamed)));
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		InteractionResult retval;
		if(this.level().isClientSide())
			retval = InteractionResult.SUCCESS;
		else
			retval = InteractionResult.SUCCESS_SERVER;
		Item item = itemstack.getItem();
		if(itemstack.is(PraporModItems.GUIDE_BOOK.get())){
			GuideBookItem.addToBook(sourceentity, this, 1);
		}
		if (itemstack.getItem() instanceof SpawnEggItem) {
			retval = super.mobInteract(sourceentity, hand);
		} else if (this.level().isClientSide()) {
			retval = (this.isTame() && this.isOwnedBy(sourceentity) || this.isFood(itemstack)) ? InteractionResult.SUCCESS : InteractionResult.PASS;
		} else {
			if (this.isTame()) {
				if (this.isOwnedBy(sourceentity)) {
					if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						this.usePlayerItem(sourceentity, hand, itemstack);
						FoodProperties foodproperties = itemstack.get(DataComponents.FOOD);
						float nutrition = foodproperties != null ? (float) foodproperties.nutrition() : 1;
						this.heal(nutrition);
						if(this.level().isClientSide())
							retval = InteractionResult.SUCCESS;
						else
							retval = InteractionResult.SUCCESS_SERVER;
					} else if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						this.usePlayerItem(sourceentity, hand, itemstack);
						this.heal(4);
						if(this.level().isClientSide())
							retval = InteractionResult.SUCCESS;
						else
							retval = InteractionResult.SUCCESS_SERVER;
					} else {
						retval = super.mobInteract(sourceentity, hand);
					}
				}
			} else if (this.isFood(itemstack)) {
				this.usePlayerItem(sourceentity, hand, itemstack);
				if (this.random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, sourceentity)) {
					this.tame(sourceentity);
					GuideBookItem.addToBook((Player) this.getOwner(), this, 2);
					this.level().broadcastEntityEvent(this, (byte) 7);
				} else {
					this.level().broadcastEntityEvent(this, (byte) 6);
				}
				this.setPersistenceRequired();
				if(this.level().isClientSide())
					retval = InteractionResult.SUCCESS;
				else
					retval = InteractionResult.SUCCESS_SERVER;
			} else {
				retval = super.mobInteract(sourceentity, hand);
				if (retval == InteractionResult.SUCCESS || retval == InteractionResult.CONSUME)
					this.setPersistenceRequired();
			}
		}
		//sourceentity.startRiding(this);
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity entity = this;
		Level world = this.level();

		PraporkaToPraporProcedure.execute(world, entity, sourceentity, itemstack);
		return retval;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		PraporSlowFallingProcedure.execute(this);
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose pose) {
		return super.getDefaultDimensions(pose).scale(1f);
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		PraporEntity retval = PraporModEntities.PRAPOR.get().create(serverWorld, EntitySpawnReason.BREEDING);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), EntitySpawnReason.BREEDING, null);
		return retval;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return Objects.equals(Blocks.SAND.asItem(), stack.getItem());
	}

	@Override
	public void travel(Vec3 dir) {
		Entity entity = this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
		if (this.isVehicle()) {
			this.setYRot(entity.getYRot());
			this.yRotO = this.getYRot();
			this.setXRot(entity.getXRot() * 0.5F);
			this.setRot(this.getYRot(), this.getXRot());
			this.yBodyRot = entity.getYRot();
			this.yHeadRot = entity.getYRot();
			if (entity instanceof LivingEntity passenger) {
				this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
				float forward = passenger.zza;
				float strafe = passenger.xxa;
				super.travel(new Vec3(strafe, 0, forward));
			}
			double d1 = this.getX() - this.xo;
			double d0 = this.getZ() - this.zo;
			float f1 = (float) Math.sqrt(d1 * d1 + d0 * d0) * 4;
			if (f1 > 1.0F)
				f1 = 1.0F;
			this.walkAnimation.setSpeed(this.walkAnimation.speed() + (f1 - this.walkAnimation.speed()) * 0.4F);
			this.walkAnimation.position(this.walkAnimation.position() + this.walkAnimation.speed());
			this.calculateEntityAnimation(true);
			return;
		}
		super.travel(dir);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
		event.register(PraporModEntities.PRAPOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.WORLD_SURFACE,
				(entityType, world, reason, pos, random) -> ((world.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON)
				|| world.getBlockState(pos.below()).is(Blocks.SAND)
				|| world.getBlockState(pos.below()).is(Blocks.SANDSTONE)) && world.getRawBrightness(pos, 0) > 8), RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 10);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.STEP_HEIGHT, 1.9);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
		return builder;
	}

	private PlayState movementPredicate(AnimationTest<PraporEntity> event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() && this.onGround() && !this.isAggressive() && !this.isSprinting())) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("death"));
			}
			if (this.isShiftKeyDown()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			if (this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("run"));
			}
			if (!this.onGround()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("flight"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("run"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationTest<PraporEntity> event) {
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
			return event.setAndContinue(RawAnimation.begin().thenPlay("attack"));
		}
		return PlayState.CONTINUE;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationTest<PraporEntity> event) {
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
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 42) {
			this.remove(PraporEntity.RemovalReason.KILLED);
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
