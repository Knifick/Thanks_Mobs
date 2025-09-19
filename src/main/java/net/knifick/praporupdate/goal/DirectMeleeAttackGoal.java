package net.knifick.praporupdate.goal;

import net.knifick.praporupdate.PraporMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;

public class DirectMeleeAttackGoal extends Goal {
    private static final Logger LOGGER = PraporMod.LOGGER;

    private final PathfinderMob mob;
    private final double speedModifier; // фактичесный множитель применяется к атрибуту скорости
    private final boolean followEvenIfNotSeen;

    private Path path;
    private double lastPathedX, lastPathedY, lastPathedZ;
    private int ticksUntilNextPathRecalc;
    private int ticksUntilNextAttack;
    private long lastCanUseCheck;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = true;

    private boolean usingDirectChase = false;
    private double lastDistanceSqToTarget = Double.MAX_VALUE;
    private int stuckTicks = 0;
    private int fallbackTicks = 0;

    private int lastSetWantedTick = 0;
    private double lastWantedX = Double.NaN, lastWantedY = Double.NaN, lastWantedZ = Double.NaN;

    private static final int DEFAULT_ATTACK_COOLDOWN = 20;

    public DirectMeleeAttackGoal(PathfinderMob mob, double speedModifier, boolean followEvenIfNotSeen) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.followEvenIfNotSeen = followEvenIfNotSeen;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        long time = this.mob.level().getGameTime();
        if (time - this.lastCanUseCheck < 20L) {
            return false;
        }
        this.lastCanUseCheck = time;

        LivingEntity target = this.mob.getTarget();
        if (target == null) return false;
        if (!target.isAlive()) return false;

        this.path = this.mob.getNavigation().createPath(target, 0);
        return this.path != null || this.mob.isWithinMeleeAttackRange(target);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return false;
        if (!target.isAlive()) return false;
        if (!this.followEvenIfNotSeen) {
            return !this.mob.getNavigation().isDone();
        } else {
            if (target instanceof Player) {
                Player p = (Player) target;
                if (p.isSpectator() || p.isCreative()) return false;
            }
            return true;
        }
    }

    @Override
    public void start() {
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalc = 0;
        this.ticksUntilNextAttack = 0;
        this.usingDirectChase = false;
        this.lastDistanceSqToTarget = Double.MAX_VALUE;
        this.stuckTicks = 0;
        this.fallbackTicks = 0;
        this.lastSetWantedTick = 0;
        this.lastWantedX = Double.NaN;

        if (this.path != null) {
            // используем реальную скорость (атрибут * модификатор)
            double realSpeed = this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.speedModifier;
            this.mob.getNavigation().moveTo(this.path, realSpeed);
        }
    }

    @Override
    public void stop() {
        LivingEntity target = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            this.mob.setTarget(null);
        }
        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
        this.usingDirectChase = false;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        this.ticksUntilNextPathRecalc = Math.max(this.ticksUntilNextPathRecalc - 1, 0);
        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        if (this.fallbackTicks > 0) this.fallbackTicks--;

        double dx = target.getX() - this.mob.getX();
        double dy = target.getY() - (this.mob.getY() + this.mob.getEyeHeight());
        double dz = target.getZ() - this.mob.getZ();
        double distanceSq = dx * dx + dy * dy + dz * dz;

        boolean canSee = this.mob.getSensing().hasLineOfSight(target);
        if (canSee && this.fallbackTicks == 0) {
            if (!this.usingDirectChase) {
                this.usingDirectChase = true;
                this.mob.getNavigation().stop();
                this.lastDistanceSqToTarget = Double.MAX_VALUE;
                this.stuckTicks = 0;
            }

            MoveControl mc = this.mob.getMoveControl();

            // realSpeed — абсолютная скорость, которую передаём контроллеру
            float realSpeed = (float) (this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.speedModifier);

            // ДЕБАУНС: обновляем цель движения только если цель ушла существенно или прошло >= 3 тикa
            boolean targetMoved = Double.isNaN(this.lastWantedX)
                    || target.distanceToSqr(this.lastWantedX, this.lastWantedY, this.lastWantedZ) > 0.0625D; // 0.25^2
            boolean tickAllow = (this.mob.level().getGameTime() - this.lastSetWantedTick) >= 3L;

            if (targetMoved || tickAllow) {
                mc.setWantedPosition(target.getX(), target.getY(), target.getZ(), realSpeed);
                this.lastSetWantedTick = (int) this.mob.level().getGameTime();
                this.lastWantedX = target.getX();
                this.lastWantedY = target.getY();
                this.lastWantedZ = target.getZ();

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("DirectMelee: setWantedPosition tick={}, tx={}, tz={}, moved={}, tickAllow={}, realSpeed={}",
                            this.lastSetWantedTick, this.lastWantedX, this.lastWantedZ, targetMoved, tickAllow, realSpeed);
                }
            }

            // detector stuck: проверяем уменьшается ли расстояние
            if (distanceSq + 1e-6 >= this.lastDistanceSqToTarget) {
                this.stuckTicks++;
            } else {
                this.stuckTicks = 0;
            }
            this.lastDistanceSqToTarget = distanceSq;

            if (this.stuckTicks >= 10) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("DirectMeleeAttackGoal: detected stuck, switching to pathfinding fallback. stuckTicks={}, distanceSq={}",
                            this.stuckTicks, distanceSq);
                }
                this.usingDirectChase = false;
                this.fallbackTicks = 20 + this.mob.getRandom().nextInt(20);
                double realSpeedD = this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.speedModifier;
                boolean moved = this.mob.getNavigation().moveTo(target, realSpeedD);
                if (!moved) {
                    this.failedPathFindingPenalty += 10;
                }
                return;
            }
        } else {
            if (this.usingDirectChase) {
                this.usingDirectChase = false;
            }

            if ((this.followEvenIfNotSeen || canSee) && this.ticksUntilNextPathRecalc <= 0
                    && (this.lastPathedX == 0.0D && this.lastPathedY == 0.0D && this.lastPathedZ == 0.0D
                    || target.distanceToSqr(this.lastPathedX, this.lastPathedY, this.lastPathedZ) >= 1.0D
                    || this.mob.getRandom().nextFloat() < 0.05F)) {

                this.lastPathedX = target.getX();
                this.lastPathedY = target.getY();
                this.lastPathedZ = target.getZ();

                this.ticksUntilNextPathRecalc = 4 + this.mob.getRandom().nextInt(7);

                double d0 = this.mob.distanceToSqr(target);
                if (this.canPenalize) {
                    this.ticksUntilNextPathRecalc += this.failedPathFindingPenalty;
                    if (this.mob.getNavigation().getPath() != null) {
                        Path p = this.mob.getNavigation().getPath();
                        if (p.getEndNode() != null && target.distanceToSqr(p.getEndNode().x, p.getEndNode().y, p.getEndNode().z) < 1.0D) {
                            this.failedPathFindingPenalty = 0;
                        } else {
                            this.failedPathFindingPenalty += 10;
                        }
                    } else {
                        this.failedPathFindingPenalty += 10;
                    }
                }

                if (d0 > 1024.0D) {
                    this.ticksUntilNextPathRecalc += 10;
                } else if (d0 > 256.0D) {
                    this.ticksUntilNextPathRecalc += 5;
                }

                double realSpeedD = this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.speedModifier;
                if (!this.mob.getNavigation().moveTo(target, realSpeedD)) {
                    this.ticksUntilNextPathRecalc += 15;
                }

                this.ticksUntilNextPathRecalc = this.adjustedTickDelay(this.ticksUntilNextPathRecalc);
            }
        }

        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        this.checkAndPerformAttack(target);
    }

    protected void checkAndPerformAttack(LivingEntity target) {
        if (this.canPerformAttack(target)) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            if(target.level() instanceof ServerLevel level)
                this.mob.doHurtTarget(level, target);
        }
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(DEFAULT_ATTACK_COOLDOWN);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean canPerformAttack(LivingEntity target) {
        return this.isTimeToAttack() && this.mob.isWithinMeleeAttackRange(target) && this.mob.getSensing().hasLineOfSight(target);
    }

    public int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }
}
