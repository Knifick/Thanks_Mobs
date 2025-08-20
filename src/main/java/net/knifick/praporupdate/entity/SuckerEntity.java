
package net.knifick.praporupdate.entity;

import net.knifick.praporupdate.goal.SuckerSuckGoal;
import net.knifick.praporupdate.init.PraporModEntities;
import net.knifick.praporupdate.network.PraporModVariables;
import net.knifick.praporupdate.procedures.DanceRetirnerProcedure;
import net.knifick.praporupdate.procedures.PraporSlowFallingProcedure;
import net.knifick.praporupdate.procedures.PraporTamedProcedureProcedure;
import net.knifick.praporupdate.procedures.PraporkaToPraporProcedure;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SuckerEntity extends TamableAnimal implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_isTamed = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> IS_SUCK = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Optional<UUID>> PLAYER_UUID = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.OPTIONAL_UUID);
	public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public Vec3 playerPos;
	public String animationprocedure = "empty";
	private static final int MAX_ITEMS = 5;
	private int nextInventoryIndex = 0;
	public final SimpleContainer inventory = new SimpleContainer(MAX_ITEMS);

	public SuckerEntity(EntityType<SuckerEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SHOOT, false);
		builder.define(ANIMATION, "undefined");
		builder.define(TEXTURE, "sucker_idle");
		builder.define(PLAYER_UUID, Optional.empty());
		builder.define(DATA_isTamed, false);
		builder.define(COLOR, -1);
		builder.define(IS_SUCK, true);
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	public void setPlayerUUID(@Nullable UUID player) {
		this.entityData.set(PLAYER_UUID, Optional.ofNullable(player));
	}

	@Nullable
	public UUID getPlayerUUID() {
		return this.entityData.get(PLAYER_UUID).orElse(null);
	}

	public int getColor() {
		return this.entityData.get(COLOR);
	}

	public void setColor(int color) {
		this.entityData.set(COLOR, color);
	}

	public boolean isSuck() {
		return this.entityData.get(IS_SUCK);
	}

	public void setSuck(boolean is) {
		this.entityData.set(IS_SUCK, is);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new SuckerSuckGoal(this, 10, 0.1));
		this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1, (float) 10, (float) 2)
		{
			@Override
			public boolean canUse() {
				return super.canUse() && !SuckerEntity.this.getTexture().equals("sucker_suck");
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && !SuckerEntity.this.getTexture().equals("sucker_suck");
			}
		});
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8){
			@Override
			public boolean canUse() {
				return super.canUse() && !SuckerEntity.this.getTexture().equals("sucker_suck");
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && !SuckerEntity.this.getTexture().equals("sucker_suck");
			}
		});
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && !SuckerEntity.this.getTexture().equals("sucker_suck");
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && !SuckerEntity.this.getTexture().equals("sucker_suck");
			}
		});
		this.goalSelector.addGoal(5, new FloatGoal(this));
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float f) {
		return super.getPassengerAttachmentPoint(entity, dimensions, f).add(0, -0.1f, 0);
	}

	@Override
	public SoundEvent getAmbientSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:sucker_idle"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:sucker_hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:sucker_hurt"));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FALL))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		if(this.getPlayerUUID() != null)
			compound.putUUID("player_uuid", this.getPlayerUUID());
		compound.putBoolean("DataisTamed", this.entityData.get(DATA_isTamed));
		compound.putBoolean("is_suck", this.entityData.get(IS_SUCK));

		// сохраняем инвентарь
		compound.put("Inventory", inventory.createTag(this.level().registryAccess()));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("DataisTamed"))
			this.entityData.set(DATA_isTamed, compound.getBoolean("DataisTamed"));
		if (compound.contains("player_uuid"))
			this.setPlayerUUID(compound.getUUID("player_uuid"));
		if (compound.contains("is_suck"))
			this.entityData.set(IS_SUCK, compound.getBoolean("is_suck"));

		// загружаем инвентарь
		if (compound.contains("Inventory")) {
			inventory.fromTag(compound.getList("Inventory", 10), this.level().registryAccess());
		}
	}

	public void addItemToInventory(ItemStack stack) {
		ItemStack copy = stack.copy();

		// 1. Попробуем найти слот с таким же предметом, который ещё не полный
		for (int i = 0; i < MAX_ITEMS; i++) {
			ItemStack slotStack = inventory.getItem(i);
			if (!slotStack.isEmpty() && ItemStack.isSameItem(slotStack, copy) && slotStack.getCount() < slotStack.getMaxStackSize()) {
				int spaceLeft = slotStack.getMaxStackSize() - slotStack.getCount();
				int toAdd = Math.min(spaceLeft, copy.getCount());
				slotStack.grow(toAdd);
				copy.shrink(toAdd);
				if (copy.isEmpty()) return; // Всё добавили
			}
		}

		// 2. Если остался предмет, ищем пустой слот
		for (int i = 0; i < MAX_ITEMS; i++) {
			ItemStack slotStack = inventory.getItem(i);
			if (slotStack.isEmpty()) {
				inventory.setItem(i, copy);
				return;
			}
		}

		// 3. Если слотов нет, перезаписываем по кругу
		inventory.setItem(nextInventoryIndex, copy);
		if (!(this.getOwner() instanceof ServerPlayer player)) return;
		PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);
		vars.suckCount += copy.getCount();
		vars.syncPlayerVariables(player);
		checkAchievement(player);
		nextInventoryIndex = (nextInventoryIndex + 1) % MAX_ITEMS;
	}

	public void checkAchievement(ServerPlayer player) {
		PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);

		System.out.println(vars.suckCount);
		if (vars.suckCount >= 1000) {
			AdvancementHolder _adv = player.server.getAdvancements().get(ResourceLocation.parse("prapor:mouth_job"));
			AdvancementProgress _ap = player.getAdvancements().getOrStartProgress(_adv);
			if (!_ap.isDone()) {
				for (String criteria : _ap.getRemainingCriteria())
					player.getAdvancements().award(_adv, criteria);
			}
		}
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

		if(sourceentity.isShiftKeyDown()){
			this.setSuck(!this.isSuck());
			return retval;
		}

		byte result = isCleanItemStack(itemstack);
		if (result != 0 && this.getColor()!=-1) {
			this.setColor(-1);
			if (result == 1) {
				this.playSound(SoundEvents.BUCKET_EMPTY);
				for (int i = 0; i < 8; i++) {
					double dx = (random.nextDouble() - 0.5) * 1.5;
					double dy = random.nextDouble() * 0.5 + 1.5;
					double dz = (random.nextDouble() - 0.5) * 1.5;
					level().addParticle(ParticleTypes.SPLASH,
							getX(), getY() + 1.0, getZ(),
							dx, dy, dz);
				}
				if (!sourceentity.getAbilities().instabuild) {
					sourceentity.setItemInHand(hand, new ItemStack(Items.BUCKET));
				}
			}
			else if (result == 2) {
				this.playSound(SoundEvents.BRUSH_GENERIC);
				for (int i = 0; i < 8; i++) {
					double dx = (random.nextDouble() - 0.5) * 1.5;
					double dy = random.nextDouble() * 0.5 + 1.5;
					double dz = (random.nextDouble() - 0.5) * 1.5;
					level().addParticle(
							new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.RED_CONCRETE.defaultBlockState()),
							getX(), getY() + 1.0, getZ(),
							dx, dy, dz
					);
				}
				if (!sourceentity.getAbilities().instabuild) {
					itemstack.setDamageValue(itemstack.getDamageValue() + 1);
				}
			}
			return InteractionResult.sidedSuccess(level().isClientSide);
		}

		if (itemstack.getItem() instanceof DyeItem dye) {
			int rgb = dye.getDyeColor().getTextColor(); // готовый int цвет
			this.setColor(rgb);

			this.playSound(SoundEvents.DYE_USE);

			int color = this.getColor();
			double r = (color >> 16 & 0xFF) / 255.0;
			double g = (color >> 8 & 0xFF) / 255.0;
			double b = (color & 0xFF) / 255.0;

			for (int i = 0; i < 8; i++) {
				double dx = (random.nextDouble() - 0.5) * 3.5;
				double dy = random.nextDouble() * 0.5 + 3.5;
				double dz = (random.nextDouble() - 0.5) * 3.5;
				level().addParticle(new DustParticleOptions(new Vector3f((float) r, (float) g, (float) b), 1.0F),
						getX(), getY() + 1.0, getZ(),
						dx, dy, dz);
			}
			if (!sourceentity.getAbilities().instabuild) {
				itemstack.shrink(1);
			}
			return InteractionResult.sidedSuccess(level().isClientSide);
		}

		// если сущность уже приручена и это её владелец
		if (!this.level().isClientSide() && this.isTame() && this.isOwnedBy(sourceentity)) {
			// пробуем достать предмет с конца
			ItemStack toDrop = this.dropLastItem();
			if (!toDrop.isEmpty()) {
				// выбросить предмет к игроку
				ItemEntity itementity = new ItemEntity(
						this.level(),
						this.getX(), this.getY() + 1.0D, this.getZ(),
						toDrop
				);
				// небольшой толчок в сторону игрока
				Vec3 motion = new Vec3(sourceentity.getX() - this.getX(), sourceentity.getEyeY() - this.getY(), sourceentity.getZ() - this.getZ())
						.normalize()
						.scale(0.3);
				itementity.setDeltaMovement(motion);

				this.level().addFreshEntity(itementity);

				CompoundTag data = itementity.getPersistentData();
				data.putDouble("unsuck_timer", 0);
			}
		}

		return retval;
	}

	private byte isCleanItemStack(ItemStack itemStack){
		byte ret = 0;
		if(itemStack.is(Items.WATER_BUCKET)) ret = 1;
		else if (itemStack.is(Items.BRUSH)) ret = 2;
		return ret;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		PraporSlowFallingProcedure.execute(this);
		if(getColor()==0){
			double dx = (random.nextDouble() - 0.5) * 1.5;
			double dy = (random.nextDouble() - 0.5) * 1.5;
			double dz = (random.nextDouble() - 0.5) * 1.5;
			level().addParticle(new DustParticleOptions(new Vector3f(0f, 0f, 0f), 1.0F),
					getX()+dx, getY()+dy+1, getZ()+dz,
					dx, dy, dz);
		}
		if("jeb_".equals(getDisplayName().getString())){
			float hue = (tickCount % 360) / 360.0F;
			int rgb = java.awt.Color.HSBtoRGB(hue, 1.0F, 1.0F);
			setColor(rgb);
		}
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose pose) {
		return super.getDefaultDimensions(pose).scale(1f);
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		SuckerEntity retval = PraporModEntities.SUCKER.get().create(serverWorld);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null);
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

	public ItemStack dropLastItem() {
		for (int i = MAX_ITEMS - 1; i >= 0; i--) {
			ItemStack stack = inventory.getItem(i);
			if (!stack.isEmpty()) {
				inventory.setItem(i, ItemStack.EMPTY);
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	public void addItem(ItemStack stack) {
		ItemStack copy = stack.copy();
		copy.setCount(1); // если нужно по 1 предмету, иначе убери

		// Ищем свободный слот
		for (int i = 0; i < MAX_ITEMS; i++) {
			if (inventory.getItem(i).isEmpty()) {
				inventory.setItem(i, copy);
				return;
			}
		}

		// Если все занято — сдвигаем всё и кладём новый в конец
		for (int i = 1; i < MAX_ITEMS; i++) {
			inventory.setItem(i - 1, inventory.getItem(i));
		}
		inventory.setItem(MAX_ITEMS - 1, copy);
	}

	public SimpleContainer getInventory() {
		return inventory;
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
		event.register(PraporModEntities.PRAPOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && world.getRawBrightness(pos, 0) > 8), RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.15);
		builder = builder.add(Attributes.MAX_HEALTH, 10);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.STEP_HEIGHT, 1.9);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
		return builder;
	}

	private <E extends GeoAnimatable> PlayState movementPredicate(AnimationState<E> state) {
		if (isSucking()) return PlayState.STOP;  // <-- ключевая строка

		if (this.isDeadOrDying()) {
			return state.setAndContinue(RawAnimation.begin().thenPlay("death"));
		}
		if (!this.onGround()) {
			return state.setAndContinue(RawAnimation.begin().thenLoop("flight"));
		}
		if (this.isSprinting() || (this.isAggressive() && state.isMoving())) {
			return state.setAndContinue(RawAnimation.begin().thenLoop("run"));
		}
		if ((state.isMoving() || !(state.getLimbSwingAmount() >= -0.1F && state.getLimbSwingAmount() <= 0.1F)) && this.onGround() && !this.isAggressive() && !this.isSprinting()) {
			return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
		}
		return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
	}

	private boolean isSucking() {
		return "sucker_suck".equals(this.getTexture());
	}

	private <E extends GeoAnimatable> PlayState attackPredicate(AnimationState<E> state) {
		if (isSucking()) return PlayState.STOP;  // <-- чтобы атака не перебивала "suck"
		if (this.swinging) {
			state.getController().forceAnimationReset();
			return state.setAndContinue(RawAnimation.begin().thenPlay("attack"));
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private <E extends GeoAnimatable> PlayState suckPredicate(AnimationState<E> state) {
		if (isSucking()) {
			return state.setAndContinue(RawAnimation.begin().thenLoop("suck"));
		}
		return PlayState.STOP;
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.triggerAnim("triggers", animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "move",   5, this::movementPredicate));
		controllers.add(new AnimationController<>(this, "attack", 0, this::attackPredicate));
		controllers.add(new AnimationController<>(this, "suck",   5, this::suckPredicate));

		controllers.add(new AnimationController<>(this, "triggers", 0, s -> PlayState.STOP)
				.triggerableAnim("dance", RawAnimation.begin().thenPlay("dance")));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
