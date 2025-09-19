
package net.knifick.praporupdate.entity;

import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.item.GuideBookItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.*;
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

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.tags.BlockTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;

import net.knifick.praporupdate.procedures.GolemAIReturnerProcedure;
import net.knifick.praporupdate.procedures.BrolemPerTickProcedure;
import net.knifick.praporupdate.procedures.BrolemOnSpawnProcedure;
import net.knifick.praporupdate.procedures.BrolemOnRCMProcedure;
import net.knifick.praporupdate.procedures.BrolemOnDeathProcedure;
import net.knifick.praporupdate.init.PraporModEntities;

import javax.annotation.Nullable;

import java.util.List;

public class BrolemEntity extends TamableAnimal implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(BrolemEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(BrolemEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BrolemEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_IsAlive = SynchedEntityData.defineId(BrolemEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> DATA_OnBuilding = SynchedEntityData.defineId(BrolemEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_xs = SynchedEntityData.defineId(BrolemEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> DATA_zs = SynchedEntityData.defineId(BrolemEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public BrolemEntity(EntityType<BrolemEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SHOOT, false);
		builder.define(ANIMATION, "undefined");
		builder.define(TEXTURE, "druzhochek");
		builder.define(DATA_IsAlive, false);
		builder.define(DATA_OnBuilding, false);
		builder.define(DATA_xs, "");
		builder.define(DATA_zs, "");
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
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected boolean canPerformAttack(LivingEntity entity) {
				return this.isTimeToAttack() && this.mob.distanceToSqr(entity) < (this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth()) && this.mob.getSensing().hasLineOfSight(entity);
			}

			@Override
			public boolean canUse() {
				double x = BrolemEntity.this.getX();
				double y = BrolemEntity.this.getY();
				double z = BrolemEntity.this.getZ();
				Entity entity = BrolemEntity.this;
				Level world = BrolemEntity.this.level();
				return super.canUse() && GolemAIReturnerProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = BrolemEntity.this.getX();
				double y = BrolemEntity.this.getY();
				double z = BrolemEntity.this.getZ();
				Entity entity = BrolemEntity.this;
				Level world = BrolemEntity.this.level();
				return super.canContinueToUse() && GolemAIReturnerProcedure.execute(entity);
			}

		});
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this) {
			@Override
			public boolean canUse() {
				double x = BrolemEntity.this.getX();
				double y = BrolemEntity.this.getY();
				double z = BrolemEntity.this.getZ();
				Entity entity = BrolemEntity.this;
				Level world = BrolemEntity.this.level();
				return super.canUse() && GolemAIReturnerProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = BrolemEntity.this.getX();
				double y = BrolemEntity.this.getY();
				double z = BrolemEntity.this.getZ();
				Entity entity = BrolemEntity.this;
				Level world = BrolemEntity.this.level();
				return super.canContinueToUse() && GolemAIReturnerProcedure.execute(entity);
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Monster.class, false, false) {
			@Override
			public boolean canUse() {
				double x = BrolemEntity.this.getX();
				double y = BrolemEntity.this.getY();
				double z = BrolemEntity.this.getZ();
				Entity entity = BrolemEntity.this;
				Level world = BrolemEntity.this.level();
				return super.canUse() && GolemAIReturnerProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = BrolemEntity.this.getX();
				double y = BrolemEntity.this.getY();
				double z = BrolemEntity.this.getZ();
				Entity entity = BrolemEntity.this;
				Level world = BrolemEntity.this.level();
				return super.canContinueToUse() && GolemAIReturnerProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1, (float) 10, (float) 2) {
			@Override
			public boolean canUse() {
				double x = BrolemEntity.this.getX();
				double y = BrolemEntity.this.getY();
				double z = BrolemEntity.this.getZ();
				Entity entity = BrolemEntity.this;
				Level world = BrolemEntity.this.level();
				return super.canUse() && GolemAIReturnerProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = BrolemEntity.this.getX();
				double y = BrolemEntity.this.getY();
				double z = BrolemEntity.this.getZ();
				Entity entity = BrolemEntity.this;
				Level world = BrolemEntity.this.level();
				return super.canContinueToUse() && GolemAIReturnerProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8) {
			@Override
			public boolean canUse() {
				double x = BrolemEntity.this.getX();
				double y = BrolemEntity.this.getY();
				double z = BrolemEntity.this.getZ();
				Entity entity = BrolemEntity.this;
				Level world = BrolemEntity.this.level();
				return super.canUse() && GolemAIReturnerProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = BrolemEntity.this.getX();
				double y = BrolemEntity.this.getY();
				double z = BrolemEntity.this.getZ();
				Entity entity = BrolemEntity.this;
				Level world = BrolemEntity.this.level();
				return super.canContinueToUse() && GolemAIReturnerProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				double x = BrolemEntity.this.getX();
				double y = BrolemEntity.this.getY();
				double z = BrolemEntity.this.getZ();
				Entity entity = BrolemEntity.this;
				Level world = BrolemEntity.this.level();
				return super.canUse() && GolemAIReturnerProcedure.execute(entity);
			}

			@Override
			public boolean canContinueToUse() {
				double x = BrolemEntity.this.getX();
				double y = BrolemEntity.this.getY();
				double z = BrolemEntity.this.getZ();
				Entity entity = BrolemEntity.this;
				Level world = BrolemEntity.this.level();
				return super.canContinueToUse() && GolemAIReturnerProcedure.execute(entity);
			}
		});
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:golem_steps")).get().value(), 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:golem_hit")).get().value();
	}

	@Override
	public SoundEvent getDeathSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:golem_death")).get().value();
	}

	@Override
	public void actuallyHurt(ServerLevel serverLevel, DamageSource source, float damage) {
		if (source.getDirectEntity() instanceof AbstractArrow)
			return;
		if (source.is(DamageTypes.FALL))
			return;
		super.actuallyHurt(serverLevel, source, damage);
	}

	@Override
	public void die(DamageSource source) {
		GuideBookItem.addToBook((Player) this.getOwner(), this, 2);
		super.die(source);
		BrolemOnDeathProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), source, this);
	}

	@Override
	public void addAdditionalSaveData(ValueOutput compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("DataIsAlive", this.entityData.get(DATA_IsAlive));
		compound.putBoolean("DataOnBuilding", this.entityData.get(DATA_OnBuilding));
		compound.putString("Dataxs", this.entityData.get(DATA_xs));
		compound.putString("Datazs", this.entityData.get(DATA_zs));
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);

		input.getString("Texture")
				.ifPresent(this::setTexture);
		this.entityData.set(DATA_IsAlive, input.getBooleanOr("DataIsAlive", this.entityData.get(DATA_IsAlive)));
		this.entityData.set(DATA_OnBuilding, input.getBooleanOr("DataOnBuilding", this.entityData.get(DATA_OnBuilding)));
		this.entityData.set(DATA_xs, input.getStringOr("Dataxs", this.entityData.get(DATA_xs)));
		this.entityData.set(DATA_zs, input.getStringOr("Datazs", this.entityData.get(DATA_zs)));
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
//		if(itemstack.is(PraporModItems.GUIDE_BOOK)){
//			GuideBookItem.addToBook(sourceentity, this, 1);
//		}
		if (itemstack.getItem() instanceof SpawnEggItem) {
			retval = super.mobInteract(sourceentity, hand);
		} else if (this.level().isClientSide()) {
			retval = (this.isTame() && this.isOwnedBy(sourceentity) || this.isFood(itemstack)) ? InteractionResult.SUCCESS : InteractionResult.PASS;
		} else {
			if (this.isTame()) {
				if (this.isOwnedBy(sourceentity)) {
					if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						this.usePlayerItem(sourceentity, hand, itemstack);
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
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity entity = this;
		Level world = this.level();

		if(!level().isClientSide) {
			if (itemstack.is(ItemTags.PICKAXES)) {
				if (world instanceof Level _level) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:brolem_ruins")).get().value(), SoundSource.NEUTRAL, 1, 1);
				}
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, (y + 1.5), z, 500, 1.5, 1.5, 1.5, 0.01);
				discard();
			}
		}

		BrolemOnRCMProcedure.execute(world, x, y, z, entity, sourceentity);
		return retval;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		BrolemPerTickProcedure.execute(this.getY(), this);
		this.refreshDimensions();
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		BrolemEntity retval = PraporModEntities.BROLEM.get().create(serverWorld, EntitySpawnReason.BREEDING);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), EntitySpawnReason.BREEDING, null);
		return retval;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return List.of().contains(stack.getItem());
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
		event.register(PraporModEntities.BROLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && world.getRawBrightness(pos, 0) > 8), RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 50);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 20);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 2);
		builder = builder.add(Attributes.ATTACK_KNOCKBACK, 2);
		return builder;
	}

	private PlayState movementPredicate(AnimationTest<BrolemEntity> event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving())
			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			if (this.isShiftKeyDown()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationTest<BrolemEntity> event) {
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

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData livingdata) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
		BrolemOnSpawnProcedure.execute(this);
		return retval;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationTest<BrolemEntity> event) {
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
		if (this.deathTime == 20) {
			this.remove(BrolemEntity.RemovalReason.KILLED);
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
