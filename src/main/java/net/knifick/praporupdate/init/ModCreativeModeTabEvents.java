package net.knifick.praporupdate.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = "prapor", bus = EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTabEvents {

	@SubscribeEvent
	public static void buildContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
			event.insertAfter(new ItemStack(Items.COPPER_INGOT), new ItemStack(PraporModItems.OXIDIZED_COPPER_INGOT.get()), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
			event.insertBefore(new ItemStack(Items.NETHERITE_INGOT), new ItemStack(PraporModItems.OXIDIZEDCOPPERSHEET.get()), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
		}
	}
}

