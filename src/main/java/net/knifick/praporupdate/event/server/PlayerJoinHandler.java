package net.knifick.praporupdate.event.server;

import net.knifick.praporupdate.PraporMod;
import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.item.PraporkaItem;
import net.knifick.praporupdate.network.PraporModVariables;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Map;

@EventBusSubscriber(modid = "prapor")
public class PlayerJoinHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // проверяем, был ли игрок раньше в этом мире
        if (!player.getPersistentData().getBoolean("prapor:hasJoined")) {
            player.getPersistentData().putBoolean("prapor:hasJoined", true);

            // выдаём предмет (пример: книга)
            ItemStack stack = new ItemStack(PraporModItems.GUIDE_BOOK.get());
            if (!player.addItem(stack)) {
                // если инвентарь полон - дропаем рядом
                player.drop(stack, false);
            }
        }
    }

    @SubscribeEvent
    public static void writeAdvancmentTrigger(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);

        // Защита от null
        if (vars.seenMobs == null) return;

        int count = 0;
        for (Map.Entry<String, Integer> entry : vars.seenMobs.entrySet()) {
            if (entry.getValue() != 0)
                count++;
        }

        // Определяем какой advancement выдать
        ResourceLocation resourceLocation = null;

        if (count >= vars.seenMobs.size()) {
            resourceLocation = ResourceLocation.parse("prapor:thanks_scientist");
        } else if (count > 7) {
            resourceLocation = ResourceLocation.parse("prapor:mastermind");
        } else if (count > 3) {
            resourceLocation = ResourceLocation.parse("prapor:advanced_explorer");
        } else if (count == 1) {
            resourceLocation = ResourceLocation.parse("prapor:beginner_explorer");
        }

        // Если не подходит ни под одно условие
        if (resourceLocation == null) return;

        // Получаем advancement и проверяем что он существует
        AdvancementHolder adv = player.server.getAdvancements().get(resourceLocation);
        if (adv == null) {
            PraporMod.LOGGER.warn("Advancement {} not found!", resourceLocation);
            return;
        }

        // Проверяем прогресс и выдаем achievement
        AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
        if (!ap.isDone()) {
            for (String criteria : ap.getRemainingCriteria())
                player.getAdvancements().award(adv, criteria);
        }
    }
}