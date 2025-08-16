package net.knifick.praporupdate.event.hat;

import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber
public class TooltipHider {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() == PraporModItems.HAT_HELMET.get()) {
            int offset = 0;
            if(Minecraft.getInstance().options.advancedItemTooltips)
                offset = 2;
            event.getToolTip().add(event.getToolTip().size()-offset,Component.literal(" Вы становитесь злодеем").withStyle(ChatFormatting.RED));
        }
    }

}
