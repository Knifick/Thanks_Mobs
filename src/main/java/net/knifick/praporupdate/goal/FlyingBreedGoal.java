package net.knifick.praporupdate.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class FlyingBreedGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING =
            TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();

    protected final Animal animal;
    private final Class<? extends Animal> partnerClass;
    protected final Level level;
    @Nullable
    protected Animal partner;
    private int loveTime;
    private final double speedModifier;

    public FlyingBreedGoal(Animal animal, double speedModifier) {
        this(animal, speedModifier, animal.getClass());
    }

    public FlyingBreedGoal(Animal animal, double speedModifier, Class<? extends Animal> partnerClass) {
        this.animal = animal;
        this.level = animal.level();
        this.partnerClass = partnerClass;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.animal.isInLove()) {
            return false;
        }
        this.partner = this.getFreePartner();
        return this.partner != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.partner != null
                && this.partner.isAlive()
                && this.partner.isInLove()
                && this.loveTime < 60
                && !this.partner.isPanicking();
    }

    @Override
    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }

    @Override
    public void tick() {
        if (this.partner == null) return;

        this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float) this.animal.getMaxHeadXRot());

        // Если моб летает — используем навигацию для летающих
        PathNavigation nav = this.animal.getNavigation();
        if (nav instanceof FlyingPathNavigation) {
            nav.moveTo(this.partner, this.speedModifier);
        } else {
            // fallback для обычных животных
            this.animal.getMoveControl().setWantedPosition(
                    this.partner.getX(),
                    this.partner.getY(),
                    this.partner.getZ(),
                    this.speedModifier
            );
        }

        ++this.loveTime;
        if (this.loveTime >= this.adjustedTickDelay(60)
                && this.animal.distanceToSqr(this.partner) < 9.0D) {
            this.breed();
        }
    }

    @Nullable
    private Animal getFreePartner() {
        // получаем всех животных в радиусе 8 блоков
        List<? extends Animal> list = this.level.getEntitiesOfClass(
                this.partnerClass,
                this.animal.getBoundingBox().inflate(8.0D),
                candidate -> true // тут Predicate вместо TargetingConditions
        );

        double closestDist = Double.MAX_VALUE;
        Animal found = null;

        for (Animal candidate : list) {
            if (this.animal.canMate(candidate) && !candidate.isPanicking()) {
                double dist = this.animal.distanceToSqr(candidate);
                if (dist < closestDist) {
                    found = candidate;
                    closestDist = dist;
                }
            }
        }
        return found;
    }


    protected void breed() {
        this.animal.spawnChildFromBreeding((ServerLevel) this.level, this.partner);
    }
}
