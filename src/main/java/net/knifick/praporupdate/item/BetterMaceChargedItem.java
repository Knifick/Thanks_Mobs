package net.knifick.praporupdate.item;

import net.knifick.praporupdate.PraporMod;
import net.knifick.praporupdate.init.PraporModEnchantments;
import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.util.ironkin.ScreenShakeUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BetterMaceChargedItem extends BetterMaceItem {
    private static final double BEAM_LENGTH = 12.0;
    private static final double BEAM_RADIUS = 5;
    private static final float  BEAM_DAMAGE = 6.0F;
    private static final double KNOCKBACK = 2;

    public BetterMaceChargedItem() {
        super(new Properties()
                .rarity(Rarity.EPIC)
                .durability(500)
                .component(DataComponents.TOOL, BetterMaceChargedItem.createToolProperties())
                .attributes(BetterMaceChargedItem.createAttributes()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> re = super.use(level, player, hand);

        ItemStack itemStack = player.getItemInHand(hand);
        float enchLevel = itemStack.getEnchantmentLevel(PraporModEnchantments.getEnchantment(level, PraporModEnchantments.RAGE_OF_SOULS));
        boolean isEnchanted = enchLevel != 0;
        int index = findSoulBottle(player);
        ItemStack soul = ItemStack.EMPTY;
        if(index != -1) soul = player.getInventory().getItem(index);
        boolean hasSoul = !soul.isEmpty() && index != -1;
        Vec3 eyePos = player.getEyePosition();
        Vec3 dir    = player.getViewVector(1.0F).normalize();
        Vec3 endPos = eyePos.add(dir.scale(BEAM_LENGTH));

        AABB beamAabb = new AABB(eyePos, endPos)
                .inflate(BEAM_RADIUS);

        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                beamAabb,
                e -> e != player && !e.isInvulnerable());

        for (LivingEntity target : targets) {
            Vec3 p = target.position().subtract(eyePos);
            double t = Mth.clamp(p.dot(dir), 0.0, BEAM_LENGTH);
            Vec3 closest = eyePos.add(dir.scale(t));
            double dist2 = target.position().distanceToSqr(closest);

            if (dist2 <= BEAM_RADIUS * BEAM_RADIUS) {
                DamageSource source = level.damageSources().indirectMagic(player, player);
                target.hurt(source, BEAM_DAMAGE*(enchLevel+1));
                Vec3 knockVec = target.position().subtract(closest).normalize().scale(KNOCKBACK);
                target.push(knockVec.x, knockVec.y + 0.1, knockVec.z);
            }
        }
        Vec3 direction;
        if(isEnchanted && hasSoul)
            direction = player.getViewVector(1f).scale(-0.7-enchLevel/3);
        else
            direction = player.getViewVector(1f).scale(-0.7);
        player.addDeltaMovement(direction);
        int slot = player.getInventory().selected;
        ItemStack weapon = player.getWeaponItem();
        ItemStack newWeapon = new ItemStack(PraporModItems.BETTER_MACE.get());
        if(!player.isCreative())
            newWeapon.setDamageValue(weapon.getDamageValue()+1);
        ItemEnchantments enchants = weapon.getEnchantments();
        EnchantmentHelper.setEnchantments(newWeapon, enchants);
        if (slot >= 0) {
            player.getInventory().setItem(slot, newWeapon);
        }
        level.playSound(null, player.blockPosition(),
                SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
        level.playSound(null, player.blockPosition(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.0F);
        if (isEnchanted && hasSoul){
            level.playSound(null, player.blockPosition(),
                    SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
            PraporMod.queueServerWork(3,()->{
                level.playSound(null, player.blockPosition(),
                        SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.0F);
            });
        }

        double step     = 0.25;
        double radius   = BEAM_RADIUS;
        double halfSize = radius * 0.35;

        if(!level.isClientSide && level instanceof ServerLevel level_) {
            for (double d = 0; d <= BEAM_LENGTH; d += step) {
                Vec3 pos = eyePos.add(dir.scale(d));

                double offX = (level.random.nextDouble() - 0.5) * halfSize * 2;
                double offY = (level.random.nextDouble() - 0.5) * halfSize * 2;
                double offZ = (level.random.nextDouble() - 0.5) * halfSize * 2;
                int soulCount = 1;
                if(hasSoul && isEnchanted){
                    soulCount = 10;
                    level_.sendParticles(ParticleTypes.END_ROD,
                            pos.x + offX,
                            pos.y + offY,
                            pos.z + offZ,
                            1,
                            0, 0, 0,
                            0);
                }
                level_.sendParticles(ParticleTypes.SOUL,
                        pos.x + offX,
                        pos.y + offY,
                        pos.z + offZ,
                        soulCount,
                        1, 1, 1,
                        0);
                level_.sendParticles(ParticleTypes.SONIC_BOOM,
                        pos.x + offX,
                        pos.y + offY,
                        pos.z + offZ,
                        1,
                        0, 0, 0,
                        0);
            }
        }

        if(isEnchanted && hasSoul && player.level().isClientSide) ScreenShakeUtil.startShake(20, 20.0F);
        if(isEnchanted && hasSoul && !player.isCreative()) player.getInventory().setItem(index, new ItemStack(Items.GLASS_BOTTLE));
        player.getCooldowns().addCooldown(PraporModItems.BETTER_MACE_CHARGED.get(), 15);
        return re;
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    public static int findSoulBottle(Player player) {
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(PraporModItems.SOUL_BOTTLE.get())) {
                return i;
            }
        }
        return -1;
    }
}
