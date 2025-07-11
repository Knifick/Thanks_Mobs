
package net.knifick.praporupdate.entity;

import net.knifick.praporupdate.util.narrator.MusicStopOnDeathHandler;
import net.knifick.praporupdate.util.narrator.MusicStopOnDistanceHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoEntity;

import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.EventHooks;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
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

import net.knifick.praporupdate.procedures.NarratorToAdoptProcedure;
import net.knifick.praporupdate.procedures.NarratorPerTickProcedure;
import net.knifick.praporupdate.procedures.NarratorHurtProcedure;
import net.knifick.praporupdate.init.PraporModEntities;
import net.knifick.praporupdate.goal.NarratorGoal;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NarratorEntity extends TamableAnimal implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(NarratorEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(NarratorEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(NarratorEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	private final Set<UUID> scaredPlayerUUIDs = new HashSet<>();
	private int narratorState = 0;
	private int fleeTicks = 0;
	public static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(NarratorEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_POWER_TIMER = SynchedEntityData.defineId(NarratorEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_IS_MUSIC = SynchedEntityData.defineId(NarratorEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<BlockPos> DATA_MUSIC_POS = SynchedEntityData.defineId(NarratorEntity.class, EntityDataSerializers.BLOCK_POS);
	public static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(NarratorEntity.class, EntityDataSerializers.INT);

	public NarratorEntity(EntityType<NarratorEntity> type, Level world) {
		super(type, world);
		xpReward = 10;
		setNoAi(false);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SHOOT, false);
		builder.define(ANIMATION, "undefined");
		builder.define(TEXTURE, "narator_animated");
		builder.define(DATA_IS_POWERED, true);
		builder.define(DATA_POWER_TIMER, 24000);
		builder.define(VARIANT, -1);
		builder.define(DATA_IS_MUSIC, false);
		builder.define(DATA_MUSIC_POS, new BlockPos(0,0,0));
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
		this.goalSelector.addGoal(0, new NarratorGoal(this));
		this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1){
			@Override
			public boolean canUse() {
				return NarratorEntity.this.isPowered() && !NarratorEntity.this.isMusic() && super.canUse();
			}

			@Override
			public boolean canContinueToUse() {
				return NarratorEntity.this.isPowered() && !NarratorEntity.this.isMusic() && super.canContinueToUse();
			}
		});
		this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1, (float) 10, (float) 2){
			@Override
			public boolean canUse() {
				return NarratorEntity.this.isPowered() && !NarratorEntity.this.isMusic() && super.canUse();
			}

			@Override
			public boolean canContinueToUse() {
				return NarratorEntity.this.isPowered() && !NarratorEntity.this.isMusic() && super.canContinueToUse();
			}
		});
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:narator_hit"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.death"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		NarratorHurtProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ());
		Entity immediatesourceentity = source.getDirectEntity();
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());

		// Добавьте сохранение состояния
		ListTag scaredPlayersTag = new ListTag();
		for (UUID uuid : scaredPlayerUUIDs) {
			scaredPlayersTag.add(NbtUtils.createUUID(uuid));
		}
		compound.put("ScaredPlayers", scaredPlayersTag);
		compound.putInt("NarratorState", narratorState);
		compound.putInt("FleeTicks", fleeTicks);

		compound.putBoolean("IsPowered", isPowered());
		compound.putInt("PowerTimer", getPowerTimer());

		compound.putBoolean("IsMusic", isMusic());

		compound.put("MusicPos", NbtUtils.writeBlockPos(getMusicPos()));
		compound.putInt("Variant", getVariant());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));

		// Добавьте загрузку состояния
		scaredPlayerUUIDs.clear();
		ListTag scaredPlayersTag = compound.getList("ScaredPlayers", Tag.TAG_INT_ARRAY);
		for (Tag entry : scaredPlayersTag) {
			scaredPlayerUUIDs.add(NbtUtils.loadUUID(entry));
		}
		narratorState = compound.getInt("NarratorState");
		fleeTicks = compound.getInt("FleeTicks");

		setPowered(compound.getBoolean("IsPowered"));
		setPowerTimer(compound.getInt("PowerTimer"));

		setMusic(compound.getBoolean("IsMusic"));

		if(compound.contains("MusicPos"))
			setMusicPos(NbtUtils.readBlockPos(compound, "MusicPos").orElse(BlockPos.ZERO));
		setVariant(compound.getInt("Variant"));
	}

	public Set<UUID> getScaredPlayers() {
		return scaredPlayerUUIDs;
	}

	public boolean isPowered() {
		return entityData.get(DATA_IS_POWERED);
	}

	public void setPowered(boolean powered) {
		entityData.set(DATA_IS_POWERED, powered);
	}

	public int getPowerTimer() {
		return entityData.get(DATA_POWER_TIMER);
	}

	public void setPowerTimer(int ticks) {
		entityData.set(DATA_POWER_TIMER, ticks);
	}

	public int getVariant() {
		return entityData.get(VARIANT);
	}

	public void setVariant(int variant) {
		entityData.set(VARIANT, variant);
	}

	public boolean isMusic() {
		return entityData.get(DATA_IS_MUSIC);
	}

	public void setMusic(boolean music) {
		entityData.set(DATA_IS_MUSIC, music);
	}

	public BlockPos getMusicPos() {
		return entityData.get(DATA_MUSIC_POS);
	}

	public void setMusicPos(BlockPos pos) {
		entityData.set(DATA_MUSIC_POS, pos);
	}

	// В классе NarratorEntity
	@Override
	public void die(DamageSource source) {
		// Останавливаем музыку перед смертью
		if (!this.level().isClientSide && this.isMusic()) {
			MusicStopOnDeathHandler.stopMusicForNarrator(this);
		}
		super.die(source);
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		InteractionResult retval = InteractionResult.sidedSuccess(this.level().isClientSide());
		Item item = itemstack.getItem();
		if (itemstack.getItem() instanceof SpawnEggItem) {
			retval = super.mobInteract(sourceentity, hand);
		} else if (this.level().isClientSide()) {
			retval = (this.isTame() && this.isOwnedBy(sourceentity) || this.isFood(itemstack)) ? InteractionResult.sidedSuccess(this.level().isClientSide()) : InteractionResult.PASS;
		} else {
			if (this.isTame()) {
				if (this.isOwnedBy(sourceentity)) {
					if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						this.usePlayerItem(sourceentity, hand, itemstack);
						FoodProperties foodproperties = itemstack.getFoodProperties(this);
						float nutrition = foodproperties != null ? (float) foodproperties.nutrition() : 1;
						this.heal(nutrition);
						retval = InteractionResult.sidedSuccess(this.level().isClientSide());
					} else if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						this.usePlayerItem(sourceentity, hand, itemstack);
						this.heal(4);
						retval = InteractionResult.sidedSuccess(this.level().isClientSide());
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
				retval = InteractionResult.sidedSuccess(this.level().isClientSide());
			} else {
				retval = super.mobInteract(sourceentity, hand);
				if (retval == InteractionResult.SUCCESS || retval == InteractionResult.CONSUME)
					this.setPersistenceRequired();
			}
		}
		if(isTame() && getOwner() != sourceentity) return InteractionResult.CONSUME;
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity entity = this;
		Level world = this.level();
		System.out.println("Server: "+world.isClientSide);
		if (this.level().isClientSide) {
			return retval; // Только визуальный отклик
		}
		NarratorToAdoptProcedure.execute(world, x, y, z, entity, sourceentity, itemstack);
		return InteractionResult.CONSUME;
	}

	private void rejectDisk(){
		MusicStopOnDistanceHandler.StopMusic(this);
		this.setMusic(false);
		this.setAnimation("empty");
		double xr = (RandomSource.create().nextDouble() * 0.2) - 0.1;
		double zr = (RandomSource.create().nextDouble() * 0.2) - 0.1;
		ItemEntity item = new ItemEntity(
				(Level) this.level(),
				this.getX(),
				this.getY()+0.5,
				this.getZ(),
				this.getItemInHand(InteractionHand.MAIN_HAND).copy());
		item.setDeltaMovement(new Vec3(xr, 0.3, zr));
		this.level().addFreshEntity(item);
		this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
	}

	@Override
	public void baseTick() {
		super.baseTick();

		if(getPowerTimer() > 0) {
			setPowerTimer(getPowerTimer()-1);
		}
		else {
			setPowered(false);
			rejectDisk();
			this.setAnimation("BatteryChange");
		}

		BlockPos blockPos = getMusicPos();
		int mobX = (int) this.getX();
		int mobY = (int) this.getY();
		int mobZ = (int) this.getZ();
		Vec3i mobPos = new Vec3i(mobX, mobY, mobZ);

		double distanceSquared = blockPos.distSqr(mobPos); // последний параметр — учитывать высоту
		double distance = Math.sqrt(distanceSquared);

		if(isMusic() && distance > 5)
		{
			rejectDisk();
		}

		NarratorPerTickProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose pose) {
		return super.getDefaultDimensions(pose).scale(1f);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
		BlockPos pos = blockPosition();
		Holder<Biome> biomeHolder = world.getBiome(pos);
		ResourceKey<Biome> biomeKey = biomeHolder.getKey();
		if(biomeKey.location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "snowy_slopes"))
		|| biomeKey.location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "snowy_beach"))
		|| biomeKey.location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "snowy_plains"))
		|| biomeKey.location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "snowy_taiga"))
		|| biomeKey.location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "ice_spikes")))
		{
			setVariant(2);
		}
		else setVariant(1);
		return retval;
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		NarratorEntity retval = PraporModEntities.NARRATOR.get().create(serverWorld);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null);
		return retval;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return false;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.isMusic()) {
			this.getNavigation().stop(); // Остановка навигации
			this.setDeltaMovement(new Vec3(0, this.getDeltaMovement().y, 0)); // Обнуление скорости
		}
		this.updateSwingTime();
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
		event.register(PraporModEntities.NARRATOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && world.getRawBrightness(pos, 0) > 8), RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 30);
		builder = builder.add(Attributes.ARMOR, 2);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 0);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.7);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

					&& !this.isSprinting()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			if (this.isShiftKeyDown()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			if (this.isSprinting()) {
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
			this.remove(NarratorEntity.RemovalReason.KILLED);
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
