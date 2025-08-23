package net.knifick.praporupdate.event.server;

import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.item.PraporkaItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

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
}