package net.knifick.praporupdate.entity;

import net.knifick.praporupdate.init.PraporModEntities;
import net.knifick.praporupdate.init.PraporModSounds;
import net.knifick.praporupdate.item.GuideBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BobEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(BobEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(BobEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(BobEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public BobEntity(EntityType<BobEntity> type, Level world) {
		super(type, world);
		xpReward = 1;
		setNoAi(false);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SHOOT, false);
		builder.define(ANIMATION, "undefined");
		builder.define(TEXTURE, "bob");
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
		this.goalSelector.addGoal(0, new RandomStrollGoal(this, 2){
			@Override
			protected Vec3 getPosition() {
				return DefaultRandomPos.getPos(this.mob, 90, 5);
			}
		});
	}

	@Override
	public void addAdditionalSaveData(ValueOutput compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
	}

	@Override
	public void readAdditionalSaveData(ValueInput compound) {
		super.readAdditionalSaveData(compound);
		compound.getString("Texture")
				.ifPresent(this::setTexture);
	}

	@Override
	public void baseTick() {
		super.baseTick();
		if(tickCount%4==0)
			this.playSound(PraporModSounds.BOB.get(),0.4f,1f);
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose pose) {
		return super.getDefaultDimensions(pose).scale(1f);
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
		event.register(
				PraporModEntities.BOB.get(),
				SpawnPlacementTypes.NO_RESTRICTIONS,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> {
					// 1) Проверяем, что это Незер
					if (!world.getLevel().dimension().equals(Level.NETHER)) {
						return false;
					}

					// 2) Проверяем высоту (крыша начинается на Y=128)
					if (pos.getY() < 128) {
						return false;
					}

					// 3) Проверяем блок под ногами
					if (!world.getBlockState(pos.below()).isSolid()) {
						return false;
					}

					// 4) Проверяем, что в радиусе 30 блоков нет другого BOB
					double radius = 120.0;
					if (!world.getEntitiesOfClass(BobEntity.class,
							new AABB(pos).inflate(radius)).isEmpty()) {
						return false;
					}

					return true;
				},
				RegisterSpawnPlacementsEvent.Operation.REPLACE
		);
	}

	@Override
	protected void actuallyHurt(ServerLevel serverLevel, DamageSource source, float p_21241_) {
		if(source.getEntity() instanceof Player player)
			GuideBookItem.addToBook(player, this, 2);
        super.actuallyHurt(serverLevel, source, p_21241_);
    }

	@Override
	public void die(DamageSource source) {
		if(source.getEntity() instanceof Player player)
			GuideBookItem.addToBook(player, this, 3);
		super.die(source);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.1);
		builder = builder.add(Attributes.MAX_HEALTH, 1040);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 0);
		builder = builder.add(Attributes.STEP_HEIGHT, 1.6);
		return builder;
	}

	private PlayState movementPredicate(AnimationTest<BobEntity> event) {
		if (this.animationprocedure.equals("empty")) {
			if (event.isMoving() && this.onGround()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
			}
			if (!this.onGround()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationTest<BobEntity> event) {
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
			this.remove(RemovalReason.KILLED);
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
