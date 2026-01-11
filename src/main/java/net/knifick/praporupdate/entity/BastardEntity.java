
package net.knifick.praporupdate.entity;

import net.knifick.praporupdate.client.screens.BookScreen;
import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.item.GuideBookItem;
import net.knifick.praporupdate.network.PraporModVariables;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
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

import java.util.EnumSet;
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
    private static final int STATE_IDLE = 0;
    private static final int STATE_SNEAK = 1;    // идём пригнувшись к сундуку
    private static final int STATE_RUMMAGE = 2;  // роемся (digging)
    private static final int STATE_FALL = 3;     // падение (falling) перед телепортом
    private static final int STATE_DROWN = 4;    // застрял в сундуке и задыхается
    private static final String LOCK_KEY = "PraporLock";

    private boolean noPush = false;       // нельзя толкать
    private boolean sneakingToChest = false;
    private BastardChestRaidGoal chestGoal;
    private int rummageTicks = 0;
    private int fallTicks = 0;
	public String animationprocedure = "empty";

	public BastardEntity(EntityType<BastardEntity> type, Level world) {
		super(type, world);
		xpReward = 1;
		setNoAi(false);
		setPersistenceRequired();
        if (this.getNavigation() instanceof GroundPathNavigation nav) {
            nav.setCanOpenDoors(true);
            nav.setCanPassDoors(true);
        }
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

    // Удобные геттеры/сеттеры поверх Synced Data
    private int getState() { return this.entityData.get(DATA_State); }
    private void setState(int s) { this.entityData.set(DATA_State, s); }

    private BlockPos getChestPos() {
        return new BlockPos(this.entityData.get(DATA_ex), this.entityData.get(DATA_ey), this.entityData.get(DATA_ez));
    }
    private void setChestPos(BlockPos pos) {
        this.entityData.set(DATA_ex, pos.getX());
        this.entityData.set(DATA_ey, pos.getY());
        this.entityData.set(DATA_ez, pos.getZ());
    }

    private static void forChestAndPartner(ServerLevel level, BlockPos pos, java.util.function.Consumer<ChestBlockEntity> action) {
        var st = level.getBlockState(pos);
        if (!(st.getBlock() instanceof net.minecraft.world.level.block.ChestBlock)) return;

        BlockPos other = null;
        var type = st.getValue(ChestBlock.TYPE); // <-- ВАЖНО
        if (type != ChestType.SINGLE) {
            var dir = ChestBlock.getConnectedDirection(st);
            other = pos.relative(dir);
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ChestBlockEntity chest) action.accept(chest);

        if (other != null) {
            BlockEntity be2 = level.getBlockEntity(other);
            if (be2 instanceof ChestBlockEntity chest2) action.accept(chest2);
        }
    }


    // Инк/дек счётчика замка (поддерживает несколько мобов)
    private static void addChestLock(ServerLevel level, BlockPos pos, int delta) {
        forChestAndPartner(level, pos, chest -> {
            var tag = chest.getPersistentData();
            int v = Math.max(0, tag.getInt(LOCK_KEY) + delta);
            tag.putInt(LOCK_KEY, v);
            chest.setChanged();
        });
    }

    private boolean chestExistsWithItems(ServerLevel level, BlockPos pos) {
        if (!level.isLoaded(pos)) return false;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ChestBlockEntity chest)) return false;
        // Двойные сундуки тоже ок: инвентарь суммируется внутри BE
        return !chest.isEmpty() && level.getBlockState(pos).getBlock() instanceof net.minecraft.world.level.block.ChestBlock;
    }

	private Predicate<Difficulty> createDifficultyPredicate() {
		return difficulty -> difficulty == Difficulty.HARD;
	}

    @Override
    public boolean isPushable() {
        return !noPush && super.isPushable();
    }

    @Override
	protected void registerGoals() {
		super.registerGoals();
        this.chestGoal = new BastardChestRaidGoal(this);
        //this.goalSelector.addGoal(2, this.chestGoal);
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, (float) 15) {
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
		this.goalSelector.addGoal(4, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(5, new MoveBackToVillageGoal(this, 0.6, false));
		this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1) {
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
		this.goalSelector.addGoal(7, new FloatGoal(this));
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return true;
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
		Entity entity = this;
		Level world = this.level();
		if(itemstack.is(PraporModItems.GUIDE_BOOK)){
			GuideBookItem.addToBook(sourceentity, this, 1);
		}

		BastardRCMProcedure.execute(world, entity, sourceentity);
		return retval;
	}

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            BlockPos cpos = getChestPos();
            if (this.level() instanceof ServerLevel sl) {
                // Если моб задохнулся в сундуке — закрыть крышку и снять замок
                if (this.getState() == STATE_DROWN) {
                    if (this.level().getBlockState(cpos).getBlock() instanceof net.minecraft.world.level.block.ChestBlock) {
                        setChestLid(sl, cpos, false);
                    }
                    addChestLock(sl, cpos, -1); // 🔧 снимаем блокировку
                }

                // Если умер во время рысканья — просто снять замок
                if (this.getState() == STATE_RUMMAGE) {
                    addChestLock(sl, cpos, -1);
                }
            }
        }
        super.die(source);
    }

    @Override
	public void baseTick() {
		super.baseTick();
        if (!this.level().isClientSide && this.getState() == STATE_DROWN) {
            BlockPos cpos = getChestPos();
            if (this.level().getBlockState(cpos).getBlock() instanceof net.minecraft.world.level.block.ChestBlock) {
                if (this.tickCount % 20 == 0) { // раз в секунду
                    this.hurt(this.level().damageSources().inWall(), 1.0F);
                }
            } else { // сундука больше нет — вылезаем
                this.noPush = false;
                this.setState(STATE_IDLE);
                this.animationprocedure = "empty";
            }
        }
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
        if (!this.level().isClientSide && this.horizontalCollision && this.level() instanceof ServerLevel sl) {
            // Если уткнулись во что-то — это может быть дверь; откроем её
            openBlockingDoorIfAny(sl);
        }
	}

    private static void playChestSoundLikeVanilla(ServerLevel level, BlockPos pos, BlockState state, SoundEvent sound) {
        ChestType type =
                state.getValue(ChestBlock.TYPE);
        if (type == ChestType.LEFT) return;

        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;

        if (type == ChestType.RIGHT) {
            Direction dir = ChestBlock.getConnectedDirection(state);
            x += 0.5D * dir.getStepX();
            z += 0.5D * dir.getStepZ();
        }

        level.playSound(
                null, x, y, z, sound, net.minecraft.sounds.SoundSource.BLOCKS,
                0.5F, level.random.nextFloat() * 0.1F + 0.9F
        );
    }

    private static void setChestLid(ServerLevel level, BlockPos pos, boolean open) {
        BlockState st = level.getBlockState(pos);
        if (st.getBlock() instanceof net.minecraft.world.level.block.ChestBlock) {
            level.blockEvent(pos, st.getBlock(), 1, open ? 1 : 0); // 1 => open, 0 => close
            level.sendBlockUpdated(pos, st, st, 3);
        }
    }

    /** Дверь закрыта? */
    private static boolean isClosedDoor(BlockState st) {
        return st.getBlock() instanceof DoorBlock && !st.getValue(DoorBlock.OPEN);
    }

    /** Открыть/закрыть дверь, корректно обновив оба полублока. Работает и для железной, и для деревянной. */
    private static void forceSetDoorOpen(ServerLevel level, BlockPos anyHalfPos, boolean open) {
        BlockState st = level.getBlockState(anyHalfPos);
        if (!(st.getBlock() instanceof DoorBlock door)) return;

        // Базовый (нижний) полублок
        BlockPos base = st.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? anyHalfPos.below() : anyHalfPos;
        BlockState lower = level.getBlockState(base);
        BlockState upper = level.getBlockState(base.above());
        if (!(lower.getBlock() instanceof DoorBlock) || !(upper.getBlock() instanceof DoorBlock)) return;

        // Пытаемся использовать API двери (если сигнатура есть в версии)
        try {
            // сигнатура в новых версиях обычно такая:
            // door.setOpen(@Nullable Entity actor, Level level, BlockState stateAtAnyHalf, BlockPos posOfThatState, boolean open)
            door.setOpen(null, level, st, anyHalfPos, open);
            return;
        } catch (Throwable ignored) {
            // Фоллбек: вручную выставляем значение OPEN для обоих полублоков
        }

        BlockState newLower = lower.setValue(DoorBlock.OPEN, open);
        BlockState newUpper = upper.setValue(DoorBlock.OPEN, open);
        // флаг 10 = UPDATE + SEND_TO_CLIENTS
        level.setBlock(base, newLower, 10);
        level.setBlock(base.above(), newUpper, 10);
    }

    /** Если перед мобом закрытая дверь, открыть её и перезапустить путь. Возвращает true, если дверь открыли. */
    private boolean openBlockingDoorIfAny(ServerLevel level) {
        // клетка прямо "вперёд" по направлению взгляда тела
        BlockPos ahead = this.blockPosition().relative(this.getDirection());

        // Проверяем обе клетки (на уровне головы и ног)
        BlockPos[] check = new BlockPos[]{ahead, ahead.above()};
        for (BlockPos p : check) {
            BlockState st = level.getBlockState(p);
            if (isClosedDoor(st)) {
                forceSetDoorOpen(level, p, true);
                // Небольшой "пинок" навигации: перестроить путь
                PathNavigation nav = this.getNavigation();
                if (nav != null && this instanceof BastardEntity) {
                    // Если есть актуальная цель для движения — переинициируем moveTo (ниже покажу где звать)
                }
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean res = super.hurt(source, amount);

        if (!this.level().isClientSide && amount > 0.0F && this.isAlive()) {
            // не сбиваем DROWN своим же урон-тика IN_WALL
            if (!source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)) {
                interruptChestRaid();
            }
        }
        return res;
    }

    private void interruptChestRaid() {
        if (this.chestGoal != null) {
            this.chestGoal.externalStop();
        }
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
        // Если проигрывается "одноразовая" процедурная анимация, этот контроллер молчит
        if (!this.animationprocedure.equals("empty")) return PlayState.STOP;

        int s = getState();

        // Пригибаемся во время подкрадывания к сундуку
        if (s == STATE_SNEAK) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("crouch"));
        }

        // Стоим и "роемся" — крутится луп "digging"
        if (s == STATE_RUMMAGE) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("digging"));
        }

        // Анимацию падения можно держать как луп до завершения fallTicks
        if (s == STATE_FALL) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("falling"));
        }

        // Внутри сундука — "drown" луп
        if (s == STATE_DROWN) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("drown"));
        }

        // Обычная походка/простой
        if (event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
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

    class BastardChestRaidGoal extends Goal {
        private final BastardEntity mob;
        private BlockPos chestPos = null;
        private BlockPos approachPos = null;
        private Direction chestFacing = Direction.NORTH;
        private final double speedMod = 0.7; // "чуть медленнее"
        private boolean lockedThisChest = false;

        public BastardChestRaidGoal(BastardEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (!(mob.level() instanceof ServerLevel level)) return false;
            if (mob.getTarget() != null) return false; // заняты боем — не до сундуков
            if (mob.getState() != STATE_IDLE) return false;

            // Ищем в радиусе follow range
            int radius = (int)Math.min(16, mob.getAttribute(Attributes.FOLLOW_RANGE).getValue());
            BlockPos base = mob.blockPosition();

            BlockPos best = null;
            double bestDist2 = Double.MAX_VALUE;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        BlockPos p = base.offset(dx, dy, dz);
                        BlockState st = level.getBlockState(p);
                        if (!(st.getBlock() instanceof ChestBlock chest)) continue;

                        // Непустой?
                        if (!chestExistsWithItems(level, p)) continue;

                        // Куда встать: прямо перед лицевой стороной сундука
                        Direction f = st.getValue(ChestBlock.FACING);
                        BlockPos front = p.relative(f);

                        // Туда можно встать? (блок проходим и есть место над ним)
                        if (!level.getBlockState(front).isAir()  && !level.getBlockState(front).is(BlockTags.REPLACEABLE)) continue;
                        if (!level.getBlockState(front.above()).isAir()) continue;

                        // Есть ли путь?
                        Vec3 goal = Vec3.atCenterOf(front);
                        Path path = mob.getNavigation().createPath(front, 1); // точка назначения — сама клетка
                        if (path == null) continue; // пусть навигатор сам дойдёт, двери откроет OpenDoorGoal

                        double d2 = mob.distanceToSqr(goal);
                        if (d2 < bestDist2) {
                            bestDist2 = d2;
                            best = p;
                            approachPos = front;
                            chestFacing = f;
                        }
                    }
                }
            }

            if (best != null) {
                chestPos = best;
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            if (!(mob.level() instanceof ServerLevel)) return;
            mob.setChestPos(chestPos);
            mob.setState(STATE_SNEAK);
            mob.sneakingToChest = true;
            mob.setPose(Pose.CROUCHING);
            moveToApproach();
        }

        private void moveToApproach() {
            Path path = mob.getNavigation().createPath(approachPos, 1);
            Vec3 entityPos = mob.position(); // или getX(), getY(), getZ()
            Vec3 targetVec = Vec3.atCenterOf(approachPos); // центр блока
            Vec3 direction = targetVec.subtract(entityPos);
            double horizontalStrength = 0.01; // настраиваемое
            if(mob.level().getBlockState(mob.blockPosition().below()).isAir()
            || (mob.getDeltaMovement().x == 0 && mob.getDeltaMovement().z == 0)){
                Vec3 motion = new Vec3(
                        direction.x * horizontalStrength,
                        mob.getDeltaMovement().y,
                        direction.z * horizontalStrength
                );
                mob.setDeltaMovement(motion);
                mob.hurtMarked = true;
            }
            if (path != null) {
                System.out.println("NOT null");
                mob.getNavigation().moveTo(path, speedMod);
            }
        }

        @Override
        public boolean canContinueToUse() {
            int s = mob.getState();
            if (s == STATE_SNEAK) return true;
            if (s == STATE_RUMMAGE) return true;
            if (s == STATE_FALL) return true;
            if (s == STATE_DROWN) return true;
            return false;
        }

        @Override
        public void tick() {
            if (!(mob.level() instanceof ServerLevel level)) return;

            switch (mob.getState()) {
                case STATE_SNEAK -> tickSneak(level);
                case STATE_RUMMAGE -> tickRummage(level);
                case STATE_FALL -> tickFall(level);
                case STATE_DROWN -> tickDrown(level);
            }
        }

        private void tickSneak(ServerLevel level) {
            if (!chestExistsWithItems(level, chestPos)) { stop(); return; }

            Vec3 here = mob.position();
            Vec3 dest = Vec3.atCenterOf(approachPos);
            double dist = here.distanceTo(dest);

            moveToApproach(); // см. обновлённый метод ниже
//            if (!mob.getNavigation().isInProgress() && dist < 2) {
//                mob.getMoveControl().setWantedPosition(dest.x, dest.y, dest.z, speedMod);
//            }
//
//            if (mob.tickCount % 20 == 0 && dist > 0.7) {
//                mob.getNavigation().recomputePath();
//            }

            // если расстояние совсем маленькое — считаем, что дошёл
            if (dist <= 0.9) {
                mob.getNavigation().stop();
                mob.setPos(dest.x, dest.y, dest.z); // аккуратно "щелкаем" в центр клетки
                Vec3 chestCenter = Vec3.atCenterOf(chestPos);
                mob.lookAt(EntityAnchorArgument.Anchor.EYES, chestCenter);

                mob.noPush = true;
                mob.sneakingToChest = false;
                mob.setPose(Pose.STANDING);

                mob.setState(STATE_RUMMAGE);
                mob.rummageTicks = 20 * 4;
            }

        }

        private void tickRummage(ServerLevel level) {
            // Если сундук исчез — заканчиваем
            if (!chestExistsWithItems(level, chestPos)) { stop(); return; }

            if (mob.rummageTicks == 20 * 4) { // начало рысканья
                BlockEntity be = level.getBlockEntity(chestPos);
                if (be instanceof ChestBlockEntity chest) {
                    FakePlayer fake = FakePlayerFactory.getMinecraft(level);

                    BlockState st = level.getBlockState(chestPos);
                    if (st.getBlock() instanceof ChestBlock) {
                        level.blockEvent(chestPos, st.getBlock(), 1, 1);
                        level.sendBlockUpdated(chestPos, st, st, 3);
                        playChestSoundLikeVanilla(level, chestPos, st, net.minecraft.sounds.SoundEvents.CHEST_OPEN);
                    }
                }
                addChestLock(level, chestPos, +1);
                lockedThisChest = true;
            }

            mob.getNavigation().stop();

            Vec3 chestCenter = Vec3.atCenterOf(chestPos);
            mob.lookAt(EntityAnchorArgument.Anchor.EYES, chestCenter);
            float yaw = chestFacing.getOpposite().toYRot();
            mob.setYRot(yaw);
            mob.setYBodyRot(yaw);
            mob.setYHeadRot(yaw);

            // Раз в 5 тиков удаляем по одному стаку (плавное "рысканье")
            if (mob.rummageTicks % 20 == 0) {
                BlockEntity be = level.getBlockEntity(chestPos);
                if (be instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest) {
                    for (int i = 0; i < chest.getContainerSize(); i++) {
                        ItemStack stack = chest.getItem(i);
                        if (!stack.isEmpty()) {
                            chest.setItem(i, ItemStack.EMPTY);
                            chest.setChanged();
                            break;
                        }
                    }
                }
            }

            // 10% шанс запустить "падение" (однажды)
            if (mob.rummageTicks == 90 && level.random.nextFloat() < 0.10f) {
                if (lockedThisChest) { addChestLock(level, chestPos, -1); lockedThisChest = false; }
                mob.setState(STATE_FALL);
                mob.fallTicks = 5;
                return;
            }

            mob.rummageTicks--;
            if (mob.rummageTicks <= 0) {
                BlockState st = level.getBlockState(chestPos);
                if (st.getBlock() instanceof ChestBlock) {
                    level.blockEvent(chestPos, st.getBlock(), 1, 0);                     // shouldBeOpen(false)
                    level.sendBlockUpdated(chestPos, st, st, 3);
                    playChestSoundLikeVanilla(level, chestPos, st, SoundEvents.CHEST_CLOSE);
                }
                if (lockedThisChest) {
                    addChestLock(level, chestPos, -1); lockedThisChest = false;
                }
                stop();
            }

        }

        private void tickFall(ServerLevel level) {
            // Если сундук уже исчез — прекращаем
            if (!chestExistsWithItems(level, chestPos)) { stop(); return; }

            mob.fallTicks--;
            if (mob.fallTicks <= 0) {
                Vec3 inside = Vec3.atCenterOf(chestPos).add(0, 0.3, 0);
                mob.teleportTo(inside.x, inside.y, inside.z);
                mob.setState(STATE_DROWN);
                mob.noPush = true;
                mob.getNavigation().stop();

                // >>> крышка должна остаться открытой
                setChestLid(level, chestPos, true);
            }

        }

        private void tickDrown(ServerLevel level) {
            // В этом состоянии дыхание уже обрабатывается в baseTick()
            // Ничего не делаем тут — просто остаёмся, пока сундук не сломан (или пока не умрём)
            if (!chestExistsWithItems(level, chestPos)) { // сундука нет — выходим
                stop();
            }
        }

        public void externalStop() {
            if (!(mob.level() instanceof ServerLevel level)) { this.stop(); return; }

            // Если прямо сейчас “роемся” или “в сундуке” — закрыть крышку
            if (chestPos != null && (mob.getState() == STATE_RUMMAGE || mob.getState() == STATE_DROWN)) {
                BlockState st = level.getBlockState(chestPos);
                if (st.getBlock() instanceof ChestBlock) {
                    level.blockEvent(chestPos, st.getBlock(), 1, 0); // close
                    level.sendBlockUpdated(chestPos, st, st, 3);
                }
            }

            // stop() уже снимает замок, сбрасывает флаги/навигацию/состояние
            this.stop();
        }

        @Override
        public void stop() {
            if (mob.level() instanceof ServerLevel lvl && chestPos != null && lockedThisChest) {
                addChestLock(lvl, chestPos, -1);
                lockedThisChest = false;
            }
            // остальное без изменений
            mob.sneakingToChest = false;
            mob.noPush = false;
            mob.setPose(Pose.STANDING);
            mob.setState(STATE_IDLE);
            mob.rummageTicks = 0;
            mob.fallTicks = 0;
            chestPos = null;
            approachPos = null;
            mob.getNavigation().stop();
        }
    }
}