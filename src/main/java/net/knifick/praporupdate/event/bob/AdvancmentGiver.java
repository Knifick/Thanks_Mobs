package net.knifick.praporupdate.event.bob;

import net.knifick.praporupdate.entity.BobEntity;
import net.knifick.praporupdate.entity.PraporEntity;
import net.knifick.praporupdate.init.PraporModEntities;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class AdvancmentGiver {
    @SubscribeEvent
    public static void onBobKill(LivingDeathEvent event){
        if (!(event.getEntity() instanceof BobEntity)) return;
        if(event.getSource().getEntity() instanceof ServerPlayer player){
            AdvancementHolder _adv = player.server.getAdvancements().get(ResourceLocation.parse("prapor:bob"));
            AdvancementProgress _ap = player.getAdvancements().getOrStartProgress(_adv);
            if (!_ap.isDone()) {
                for (String criteria : _ap.getRemainingCriteria())
                    player.getAdvancements().award(_adv, criteria);
            }
        }
    }
}
