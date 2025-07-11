package net.knifick.praporupdate.item;

import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BetterMaceChargedItem extends BetterMaceItem {
    private static final double BEAM_LENGTH = 12.0;
    private static final double BEAM_RADIUS = 5;
    private static final float  BEAM_DAMAGE = 6.0F;
    private static final double KNOCKBACK = 2;

    public BetterMaceChargedItem() {
        super(new Item.Properties()
                .rarity(Rarity.EPIC)
                .durability(500)
                .component(DataComponents.TOOL, BetterMaceChargedItem.createToolProperties())
                .attributes(BetterMaceChargedItem.createAttributes()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> re = super.use(level, player, hand);

        // 1) вычисляем параметры луча
        Vec3 eyePos = player.getEyePosition();               // точка старта
        Vec3 dir    = player.getViewVector(1.0F).normalize(); // направление
        Vec3 endPos = eyePos.add(dir.scale(BEAM_LENGTH));     // конец луча

        // 2) быстрая AABB-обёртка вокруг цилиндра ( + радиус во все стороны )
        AABB beamAabb = new AABB(eyePos, endPos)
                .inflate(BEAM_RADIUS);

        // 3) выбираем только живых сущностей
        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                beamAabb,
                e -> e != player && !e.isInvulnerable());

        // 4) фильтруем: оставляем тех, чей центр попадает
        //    в цилиндр радиусом BEAM_RADIUS вдоль отрезка [eyePos, endPos]
        for (LivingEntity target : targets) {
            Vec3 p = target.position().subtract(eyePos); // вектор от старта до цели
            double t = Mth.clamp(p.dot(dir), 0.0, BEAM_LENGTH);  // проекция на ось
            Vec3 closest = eyePos.add(dir.scale(t));            // ближайшая точка на оси
            double dist2 = target.position().distanceToSqr(closest);

            if (dist2 <= BEAM_RADIUS * BEAM_RADIUS) {
                // 5) наносим урон
                DamageSource source = level.damageSources().indirectMagic(player, player);
                target.hurt(source, BEAM_DAMAGE);

                // 6) отталкиваем от центра луча
                Vec3 knockVec = target.position().subtract(closest).normalize().scale(KNOCKBACK);
                target.push(knockVec.x, knockVec.y + 0.1, knockVec.z); // +0.1 – чуть вверх, чтобы чувствовалось
            }
        }

        Vec3 direction = player.getViewVector(1f).scale(-0.7);
        player.addDeltaMovement(direction);
        int slot = player.getInventory().selected;
        ItemStack weapon = player.getWeaponItem();
        ItemStack newWeapon = new ItemStack(PraporModItems.BETTER_MACE.get());
        newWeapon.setDamageValue(weapon.getDamageValue()+1);
        if (slot >= 0) {
            player.getInventory().setItem(slot, newWeapon);
        }
        level.playSound(null, player.blockPosition(),
                SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
        level.playSound(null, player.blockPosition(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.0F);

        // параметры луча те же, что и при поиске целей
        double step     = 0.25;              // шаг между частицами, ~4 шт. на блок
        double radius   = BEAM_RADIUS;       // полу‑ширина
        double halfSize = radius * 0.35;     // лёгкая «толщина» самой частицы

        if(!level.isClientSide && level instanceof ServerLevel level_) {
            for (double d = 0; d <= BEAM_LENGTH; d += step) {
                // точка на оси
                Vec3 pos = eyePos.add(dir.scale(d));

                // слегка рандомизируем внутри цилиндра, чтобы не была идеальная линия
                double offX = (level.random.nextDouble() - 0.5) * halfSize * 2;
                double offY = (level.random.nextDouble() - 0.5) * halfSize * 2;
                double offZ = (level.random.nextDouble() - 0.5) * halfSize * 2;

                level_.sendParticles(ParticleTypes.SOUL,          // любой эффект: CRIT, END_ROD, SONIC_BOOM и т.д.
                        pos.x + offX,
                        pos.y + offY,
                        pos.z + offZ,
                        1,                           // count
                        0, 0, 0,                    // delta (скорость = 0)
                        0);                         // speed
                level_.sendParticles(ParticleTypes.SONIC_BOOM,          // любой эффект: CRIT, END_ROD, SONIC_BOOM и т.д.
                        pos.x + offX,
                        pos.y + offY,
                        pos.z + offZ,
                        1,                           // count
                        0, 0, 0,                    // delta (скорость = 0)
                        0);                         // speed
            }
        }

        player.getCooldowns().addCooldown(PraporModItems.BETTER_MACE_CHARGED.get(), 15);
        return re;
    }
}
