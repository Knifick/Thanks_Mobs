package net.knifick.praporupdate.goal;

import net.knifick.praporupdate.PraporMod;
import net.knifick.praporupdate.entity.SuckerEntity;
import net.knifick.praporupdate.init.PraporModSounds;
import net.knifick.praporupdate.network.payloads.RingSuckPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Comparator;
import java.util.List;

import static net.knifick.praporupdate.PraporMod.LOGGER;

public class SuckerSuckGoal extends Goal {
    private final SuckerEntity sucker;
    private final double radius;       // радиус поиска предметов
    private final double pullStrength; // сила притягивания
    private ItemEntity targetItem;     // ближайший предмет в поле зрения
    private int timer;

    public SuckerSuckGoal(SuckerEntity sucker, double radius, double pullStrength) {
        this.sucker = sucker;
        this.radius = radius;
        this.pullStrength = pullStrength;
    }

    @Override
    public boolean canUse() {
        return updateTarget() && sucker.isSuck();
    }

    @Override
    public boolean canContinueToUse() {
        return isValidTarget(targetItem) && sucker.isSuck();
    }

    @Override
    public void start() {
        sucker.setTexture("sucker_suck");
        sucker.playSound(PraporModSounds.SUCK.get());
        timer = 0;
    }

    @Override
    public void stop() {
        sucker.setTexture("sucker_happy");
        targetItem = null;
    }

    @Override
    public void tick() {
        if (!isValidTarget(targetItem) && !updateTarget()) {
            return;
        }
        timer++;
        if (sucker.tickCount % 4 == 0)
            sucker.playSound(PraporModSounds.SUCK.get());

        // Поворот сущности к предмету
        sucker.getLookControl().setLookAt(targetItem, 180.0F, 360.0F);

        // Эффект кольца
        PacketDistributor.sendToAllPlayers(new RingSuckPayload(
                sucker.getX(), sucker.getY(), sucker.getZ(),
                targetItem.getX(), targetItem.getY(), targetItem.getZ()
        ));

        // Притягиваем все сущности в поле зрения
        pullEntitiesInSight();

        // Если предмет рядом — "съедаем"
        if (sucker.distanceToSqr(targetItem) < 3 * 3) {
            // Кладём предмет в инвентарь
            sucker.addItemToInventory(targetItem.getItem());
            // Убираем предмет из мира
            targetItem.discard();
        }
    }


    /**
     * Проверка, что предмет валиден (жив, в радиусе, в поле зрения).
     */
    private boolean isValidTarget(ItemEntity item) {
        if (item == null || !item.isAlive() || item.getPersistentData().contains("unsuck_timer")) return false;
        return sucker.distanceToSqr(item) <= radius * radius; // убираем проверку взгляда
    }

    /**
     * Ищет ближайший предмет в радиусе, который находится в поле зрения сущности.
     */
    private boolean updateTarget() {
        List<ItemEntity> items = sucker.level().getEntitiesOfClass(
                ItemEntity.class,
                sucker.getBoundingBox().inflate(radius)
        );

        // Находим ближайший предмет в радиусе, без проверки взгляда
        targetItem = items.stream()
                .filter(this::isValidTarget)
                .min(Comparator.comparingDouble(sucker::distanceToSqr))
                .orElse(null);

        return targetItem != null;
    }

    private void pullEntitiesInSight() {
        List<Entity> entities = sucker.level().getEntities(
                (Entity) null,
                sucker.getBoundingBox().inflate(radius),
                e -> e != sucker && e.isAlive()
        );

        Vec3 lookVec = sucker.getLookAngle().normalize();
        Vec3 eyePos = sucker.getEyePosition();

        double fov = Math.cos(Math.toRadians(60)); // угол обзора ±60° (120° всего)

        for (Entity entity : entities) {
            Vec3 toEntity = entity.position().subtract(eyePos).normalize();
            double dot = lookVec.dot(toEntity);
            double angle = Math.toDegrees(Math.acos(dot));

            boolean inSight = dot > fov;

            // Лог
            LOGGER.info(
                    "[PullDebug] Target={} | dot={} | angle={}° | fov={} | inSight={}",
                    entity.getName().getString(),
                    String.format("%.3f", dot),
                    String.format("%.1f", angle),
                    String.format("%.3f", fov),
                    inSight
            );

            // Предметы: тянем всегда (или хотя бы targetItem)
            if (entity instanceof ItemEntity) {
                Vec3 toSucker = sucker.position().subtract(entity.position());
                Vec3 pull = toSucker.normalize().scale(pullStrength);
                float yOffset = (float) Math.sin(entity.tickCount * 0.3f) * 0.1f;

                entity.push(pull.x, pull.y + yOffset, pull.z);
                continue;
            }

            // Остальные: тянем только если в поле зрения
            if (!inSight) continue;

            Vec3 toSucker = sucker.position().subtract(entity.position());
            Vec3 pull = toSucker.normalize().scale(pullStrength);
            float yOffset = (float) Math.sin(entity.tickCount * 0.3f) * 0.1f;

            entity.push(pull.x / 2, pull.y / 2 + yOffset, pull.z / 2);
        }
    }

}
