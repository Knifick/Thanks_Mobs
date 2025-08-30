package net.knifick.praporupdate.event.effect;

import net.knifick.praporupdate.entity.NymphEntity;
import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.init.PraporModMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSplitEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class PurgeRealise {
    @SubscribeEvent
    public static void onPurge(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            if (entity.hasEffect(PraporModMobEffects.PURGE.getDelegate())) {
                // собираем отрицательные эффекты (в виде Holder<MobEffect>)
                List<Holder<MobEffect>> toRemove = new ArrayList<>();
                for (MobEffectInstance effectInstance : entity.getActiveEffects()) {
                    Holder<MobEffect> effect = effectInstance.getEffect();
                    if (!effect.value().isBeneficial()) {
                        toRemove.add(effect);
                    }
                }

                // снимаем эффекты
                for (Holder<MobEffect> bad : toRemove) {
                    entity.removeEffect(bad);
                }
            }
        }
    }
}

