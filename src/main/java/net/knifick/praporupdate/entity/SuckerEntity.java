package net.knifick.praporupdate.entity;

import net.knifick.praporupdate.goal.SuckerSuckGoal;
import net.knifick.praporupdate.init.PraporModEntities;
import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.network.PraporModVariables;
import net.knifick.praporupdate.item.GuideBookItem;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.joml.Vector3f;

public class SuckerEntity extends TamableAnimal implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String>  ANIMATION = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String>  TEXTURE   = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.STRING);

	public static final EntityDataAccessor<Boolean> DATA_isTamed = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> IS_SUCK      = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.BOOLEAN);
	// UUID в SynchedData храним как строку (см. обсуждение OPTIONAL_UUID/UUID сериализаторов)
	public static final EntityDataAccessor<String>  PLAYER_UUID  = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> COLOR        = SynchedEntityData.defineId(SuckerEntity.class, EntityDataSerializers.INT);

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	// инвентарь
	private static final int MAX_ITEMS = 5;
	private final SimpleContainer inventory = new SimpleContainer(MAX_ITEMS);
	private int nextInventoryIndex = 0;
	public Vec3 playerPos = Vec3.ZERO;

	// анимация/удары (как в BrolemEntity)
	private boolean swinging;
	private long lastSwing;
	public  String animationprocedure = "empty";

	public SuckerEntity(EntityType<SuckerEntity> type, Level level) {
		super(type, level);
		this.xpReward = 0;
		this.setNoAi(false);
	}

	// --------- Synced data ---------
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SHOOT, false);
		builder.define(ANIMATION, "undefined");
		builder.define(TEXTURE, "sucker_idle");

		builder.define(DATA_isTamed, false);
		builder.define(IS_SUCK, true);
		builder.define(PLAYER_UUID, ""); // пустая строка = UUID нет
		builder.define(COLOR, -1);
	}

	public void setTexture(String texture) { this.entityData.set(TEXTURE, texture); }
	public String getTexture() { return this.entityData.get(TEXTURE); }

	public void setPlayerUUID(@Nullable UUID uuid) {
		this.entityData.set(PLAYER_UUID, uuid != null ? uuid.toString() : "");
	}
	@Nullable
	public UUID getPlayerUUID() {
		String raw = this.entityData.get(PLAYER_UUID);
		try { return (raw == null || raw.isEmpty()) ? null : UUID.fromString(raw); }
		catch (IllegalArgumentException e) { return null; }
	}

	public int  getColor()          { return this.entityData.get(COLOR); }
	public void setColor(int color) { this.entityData.set(COLOR, color); }

	public boolean isSuck()           { return this.entityData.get(IS_SUCK); }
	public void    setSuck(boolean b) { this.entityData.set(IS_SUCK, b); }

	// --------- Goals (по образцу) ---------
	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new SuckerSuckGoal(this, 10, 0.1));
		this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1, 10f, 2f) {
			@Override public boolean canUse() { return super.canUse() && !isSucking(); }
			@Override public boolean canContinueToUse() { return super.canContinueToUse() && !isSucking(); }
		});
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8) {
			@Override public boolean canUse() { return super.canUse() && !isSucking(); }
			@Override public boolean canContinueToUse() { return super.canContinueToUse() && !isSucking(); }
		});
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this) {
			@Override public boolean canUse() { return super.canUse() && !isSucking(); }
			@Override public boolean canContinueToUse() { return super.canContinueToUse() && !isSucking(); }
		});
		this.goalSelector.addGoal(5, new FloatGoal(this));
	}

	private boolean isSucking() {
		// как в твоём коде — флаг «сосёт» завязан на текстуру
		return "sucker_suck".equals(this.getTexture());
	}

	// --------- Sounds ---------
	@Override public SoundEvent getAmbientSound() { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:sucker_idle")).get().value(); }
	@Override public SoundEvent getHurtSound(DamageSource ds) { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:sucker_hurt")).get().value(); }
	@Override public SoundEvent getDeathSound() { return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:sucker_hurt")).get().value(); }

	// --------- Damage hooks по примеру (не переопределяем final hurt) ---------
	@Override
	public void actuallyHurt(ServerLevel serverLevel, DamageSource source, float amount) {
		if (source.is(DamageTypes.FALL)) return;
		super.actuallyHurt(serverLevel, source, amount);
	}

	// --------- Save / Load (Value I/O как в BrolemEntity) ---------
	@Override
	public void addAdditionalSaveData(ValueOutput out) {
		super.addAdditionalSaveData(out);
		out.putString("Texture", this.getTexture());
		out.putBoolean("DataisTamed", this.entityData.get(DATA_isTamed));
		out.putBoolean("is_suck", this.entityData.get(IS_SUCK));
		UUID u = this.getPlayerUUID();
		if (u != null) out.putString("player_uuid", u.toString());
		out.putInt("Color", this.getColor());

		// Инвентарь — сохраняем как список ItemStack-ов (по слотам, включая пустые)
		var list = out.list("Inventory", ItemStack.CODEC);
		for (int i = 0; i < MAX_ITEMS; i++) list.add(this.inventory.getItem(i));
	}

	@Override
	protected void readAdditionalSaveData(ValueInput in) {
		super.readAdditionalSaveData(in);

		in.getString("Texture").ifPresent(this::setTexture);
		this.entityData.set(DATA_isTamed, in.getBooleanOr("DataisTamed", this.entityData.get(DATA_isTamed)));
		this.entityData.set(IS_SUCK,     in.getBooleanOr("is_suck",     this.entityData.get(IS_SUCK)));
		this.setColor(in.getIntOr("Color", this.getColor()));

		String uuidStr = in.getStringOr("player_uuid", null);
		if (uuidStr != null && !uuidStr.isEmpty()) {
			try { this.setPlayerUUID(UUID.fromString(uuidStr)); } catch (IllegalArgumentException ignored) { this.setPlayerUUID(null); }
		} else {
			this.setPlayerUUID(null);
		}

		// Инвентарь
		int idx = 0;
		for (ItemStack stack : in.listOrEmpty("Inventory", ItemStack.CODEC)) {
			if (idx < MAX_ITEMS) this.inventory.setItem(idx++, stack == null ? ItemStack.EMPTY : stack);
		}
		while (idx < MAX_ITEMS) this.inventory.setItem(idx++, ItemStack.EMPTY);
	}

	// --------- Interact (в точности как по примеру) ---------
	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		InteractionResult retval = this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
		Item item = itemstack.getItem();

		if (itemstack.is(PraporModItems.GUIDE_BOOK)) {
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
						// питание по новому API (через DataComponents.FOOD)
						FoodProperties food = itemstack.get(DataComponents.FOOD);
						float nutrition = food != null ? (float) food.nutrition() : 1f;
						this.heal(nutrition);
						retval = this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
					} else if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						// дублирующая ветка — оставляю как в примере BrolemEntity (см. заметку ниже)
						this.usePlayerItem(sourceentity, hand, itemstack);
						this.heal(4);
						retval = this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
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
				retval = this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
			} else {
				retval = super.mobInteract(sourceentity, hand);
				if (retval == InteractionResult.SUCCESS || retval == InteractionResult.CONSUME)
					this.setPersistenceRequired();
			}
		}

		if (sourceentity.isShiftKeyDown()) {
			this.setSuck(!this.isSuck());
			return retval;
		}

		// очистка окраски
		byte result = isCleanItemStack(itemstack);
		if (result != 0 && this.getColor() != -1) {
			this.setColor(-1);
			if (result == 1) {
				this.playSound(SoundEvents.BUCKET_EMPTY);
				for (int i = 0; i < 8; i++) {
					double dx = (random.nextDouble() - 0.5) * 1.5;
					double dy = random.nextDouble() * 0.5 + 1.5;
					double dz = (random.nextDouble() - 0.5) * 1.5;
					level().addParticle(ParticleTypes.SPLASH, getX(), getY() + 1.0, getZ(), dx, dy, dz);
				}
				if (!sourceentity.getAbilities().instabuild) {
					sourceentity.setItemInHand(hand, new ItemStack(Items.BUCKET));
				}
			} else if (result == 2) {
				this.playSound(SoundEvents.BRUSH_GENERIC);
				for (int i = 0; i < 8; i++) {
					double dx = (random.nextDouble() - 0.5) * 1.5;
					double dy = random.nextDouble() * 0.5 + 1.5;
					double dz = (random.nextDouble() - 0.5) * 1.5;
					level().addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.RED_CONCRETE.defaultBlockState()), getX(), getY() + 1.0, getZ(), dx, dy, dz);
				}
				if (!sourceentity.getAbilities().instabuild) {
					itemstack.setDamageValue(itemstack.getDamageValue() + 1);
				}
			}
			if(sourceentity.level().isClientSide)
				return InteractionResult.SUCCESS;
			else return InteractionResult.SUCCESS_SERVER;
		}

		// покраска красителями
		if (itemstack.getItem() instanceof DyeItem dye) {
			int rgb = dye.getDyeColor().getTextColor();
			this.setColor(rgb);
			this.playSound(SoundEvents.DYE_USE);

			double r = (rgb >> 16 & 0xFF) / 255.0;
			double g = (rgb >> 8 & 0xFF) / 255.0;
			double b = (rgb & 0xFF) / 255.0;

			for (int i = 0; i < 8; i++) {
				double dx = (random.nextDouble() - 0.5) * 3.5;
				double dy = random.nextDouble() * 0.5 + 3.5;
				double dz = (random.nextDouble() - 0.5) * 3.5;
				level().addParticle(new DustParticleOptions(0xFF0000, 1.0F),
						getX(), getY() + 1.0, getZ(), dx, dy, dz);
			}
			if (!sourceentity.getAbilities().instabuild) itemstack.shrink(1);
			if(sourceentity.level().isClientSide)
				return InteractionResult.SUCCESS;
			else return InteractionResult.SUCCESS_SERVER;
		}

		// если приручён и владелец — достаём последний предмет
		if (!this.level().isClientSide() && this.isTame() && this.isOwnedBy(sourceentity)) {
			ItemStack toDrop = dropLastItem();
			if (!toDrop.isEmpty()) {
				ItemEntity it = new ItemEntity(this.level(), this.getX(), this.getY() + 1.0D, this.getZ(), toDrop);
				Vec3 motion = new Vec3(sourceentity.getX() - this.getX(), sourceentity.getEyeY() - this.getY(), sourceentity.getZ() - this.getZ()).normalize().scale(0.3);
				it.setDeltaMovement(motion);
				this.level().addFreshEntity(it);
				it.getPersistentData().putDouble("unsuck_timer", 0);
			}
		}

		// кирка → спец-реакция (как в BrolemEntity)
		if (!level().isClientSide) {
			if (itemstack.is(ItemTags.PICKAXES)) {
				level().playSound(null, blockPosition(), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:brolem_ruins")).get().value(), SoundSource.NEUTRAL, 1, 1);
				if (this.level() instanceof ServerLevel sl)
					sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY() + 1.5, getZ(), 500, 1.5, 1.5, 1.5, 0.01);
				this.discard();
			}
		}

		return retval;
	}

	private byte isCleanItemStack(ItemStack st) {
		if (st.is(Items.WATER_BUCKET)) return 1;
		if (st.is(Items.BRUSH)) return 2;
		return 0;
	}

	// --------- Tick ---------
	@Override
	public void baseTick() {
		super.baseTick();
		// визуальный след для чёрного цвета
		if (getColor() == 0) {
			double dx = (random.nextDouble() - 0.5) * 1.5;
			double dy = (random.nextDouble() - 0.5) * 1.5;
			double dz = (random.nextDouble() - 0.5) * 1.5;
			level().addParticle(new DustParticleOptions( 0xFF0000, 1.0F), getX()+dx, getY()+dy+1, getZ()+dz, dx, dy, dz);
		}
		if ("jeb_".equals(getDisplayName().getString())) {
			float hue = (tickCount % 360) / 360.0F;
			int rgb = java.awt.Color.HSBtoRGB(hue, 1.0F, 1.0F);
			setColor(rgb);
		}
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose pose) { return super.getDefaultDimensions(pose).scale(1f); }

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		SuckerEntity ret = PraporModEntities.SUCKER.get().create(serverWorld, EntitySpawnReason.BREEDING);
		if (ret != null)
			ret.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(ret.blockPosition()), EntitySpawnReason.BREEDING, null);
		return ret;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return Objects.equals(Blocks.SAND.asItem(), stack.getItem());
	}

	@Override
	public void travel(Vec3 dir) {
		Entity rider = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
		if (this.isVehicle()) {
			this.setYRot(rider.getYRot());
			this.yRotO = this.getYRot();
			this.setXRot(rider.getXRot() * 0.5F);
			this.setRot(this.getYRot(), this.getXRot());
			this.yBodyRot = rider.getYRot();
			this.yHeadRot = rider.getYRot();
			if (rider instanceof LivingEntity passenger) {
				this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
				float forward = passenger.zza;
				float strafe  = passenger.xxa;
				super.travel(new Vec3(strafe, 0, forward));
			}
			double d1 = this.getX() - this.xo;
			double d0 = this.getZ() - this.zo;
			float f1 = (float)Math.sqrt(d1 * d1 + d0 * d0) * 4;
			if (f1 > 1.0F) f1 = 1.0F;
			this.walkAnimation.setSpeed(this.walkAnimation.speed() + (f1 - this.walkAnimation.speed()) * 0.4F);
			this.walkAnimation.position(this.walkAnimation.position() + this.walkAnimation.speed());
			this.calculateEntityAnimation(true);
			return;
		}
		super.travel(dir);
	}

	@Override public void aiStep() { super.aiStep(); this.updateSwingTime(); }

	// инвентарь — утилиты
	public ItemStack dropLastItem() {
		for (int i = MAX_ITEMS - 1; i >= 0; i--) {
			ItemStack s = inventory.getItem(i);
			if (!s.isEmpty()) { inventory.setItem(i, ItemStack.EMPTY); return s; }
		}
		return ItemStack.EMPTY;
	}
	public void addItemToInventory(ItemStack stack) {
		ItemStack copy = stack.copy();

		// стэк в неполный слот
		for (int i = 0; i < MAX_ITEMS; i++) {
			ItemStack slot = inventory.getItem(i);
			if (!slot.isEmpty() && ItemStack.isSameItem(slot, copy) && slot.getCount() < slot.getMaxStackSize()) {
				int space = slot.getMaxStackSize() - slot.getCount();
				int toAdd = Math.min(space, copy.getCount());
				slot.grow(toAdd);
				copy.shrink(toAdd);
				if (copy.isEmpty()) return;
			}
		}
		// пустой слот
		for (int i = 0; i < MAX_ITEMS; i++) {
			ItemStack slot = inventory.getItem(i);
			if (slot.isEmpty()) {
				inventory.setItem(i, copy);
				return;
			}
		}
		// перезапись по кругу
		inventory.setItem(nextInventoryIndex, copy);
		if (this.getOwner() instanceof Player player && !this.level().isClientSide) {
			PraporModVariables.PlayerVariables vars = ((Player)player).getData(PraporModVariables.PLAYER_VARIABLES);
			vars.suckCount += copy.getCount();
			vars.syncPlayerVariables((Player)player);
		}
		nextInventoryIndex = (nextInventoryIndex + 1) % MAX_ITEMS;
	}
	public SimpleContainer getInventory() { return inventory; }

	public static void init(RegisterSpawnPlacementsEvent event) {
		event.register(PraporModEntities.SUCKER.get(),
				SpawnPlacementTypes.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(type, world, reason, pos, random) -> (world.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && world.getRawBrightness(pos, 0) > 8),
				RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder b = Mob.createMobAttributes();
		b = b.add(Attributes.MOVEMENT_SPEED, 0.15);
		b = b.add(Attributes.MAX_HEALTH, 10);
		b = b.add(Attributes.ARMOR, 0);
		b = b.add(Attributes.ATTACK_DAMAGE, 3);
		b = b.add(Attributes.FOLLOW_RANGE, 16);
		b = b.add(Attributes.STEP_HEIGHT, 1.9);
		b = b.add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
		return b;
	}

	// --------- GeckoLib 5: предикаты/контроллеры «как у Brolem» ---------
	private PlayState movementPredicate(AnimationTest<SuckerEntity> event) {
		if (this.animationprocedure.equals("empty")) {
			if (event.isMoving()) return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			if (this.isShiftKeyDown()) return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationTest<SuckerEntity> event) {
		double d1 = this.getX() - this.xOld;
		double d0 = this.getZ() - this.zOld;
		float velocity = (float)Math.sqrt(d1 * d1 + d0 * d0);
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

	private PlayState suckPredicate(AnimationTest<SuckerEntity> event) {
		if (isSucking()) return event.setAndContinue(RawAnimation.begin().thenLoop("suck"));
		return PlayState.STOP;
	}

	String prevAnim = "empty";
	private PlayState procedurePredicate(AnimationTest<SuckerEntity> event) {
		if (!animationprocedure.equals("empty") && event.controller().getAnimationState() == AnimationController.State.STOPPED
				|| (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim)) event.controller().forceAnimationReset();
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

	public String getSyncedAnimation() { return this.entityData.get(ANIMATION); }
	public void setAnimation(String animation) { this.entityData.set(ANIMATION, animation); }

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>("movement", 4, this::movementPredicate));
		data.add(new AnimationController<>("attacking", 4, this::attackingPredicate));
		data.add(new AnimationController<>("suck", 4, this::suckPredicate));
		data.add(new AnimationController<>("procedure", 4, this::procedurePredicate));
	}

	@Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }
}
