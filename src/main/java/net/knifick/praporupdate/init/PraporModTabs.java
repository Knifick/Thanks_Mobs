
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.knifick.praporupdate.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.knifick.praporupdate.PraporMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class PraporModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PraporMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> THANKS_TAB = REGISTRY.register("thanks_tab",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.prapor.thanks_tab")).icon(() -> new ItemStack(PraporModItems.GOLD_TROPHY.get())).displayItems((parameters, tabData) -> {
				tabData.accept(PraporModItems.PRAPOR_SPAWN_EGG.get());
				tabData.accept(PraporModItems.POOKER_SPAWN_EGG.get());
				tabData.accept(PraporModItems.SOUL_SPAWN_EGG.get());
				tabData.accept(PraporModItems.NARRATOR_SPAWN_EGG.get());
				tabData.accept(PraporModItems.BASTARD_SPAWN_EGG.get());
				tabData.accept(PraporModItems.BROLEM_SPAWN_EGG.get());
				tabData.accept(PraporModItems.DARKIRONKIN_SPAWN_EGG.get());
				tabData.accept(PraporModItems.PRAPORKA.get());
				tabData.accept(PraporModItems.BATTERY.get());
				tabData.accept(PraporModItems.COPPER_WIRE.get());
				tabData.accept(PraporModItems.OXIDIZEDCOPPERSHEET.get());
				tabData.accept(PraporModItems.BOARD.get());
				tabData.accept(PraporModItems.HAT_HELMET.get());
				tabData.accept(PraporModItems.MUSIC_RECORD_N_42.get());
				//tabData.accept(PraporModItems.MUSIC_RECORD_THANKS_STREET.get());
				tabData.accept(PraporModItems.SOUL_BOTTLE.get());
				tabData.accept(PraporModItems.SPAWNER_SHARD.get());
				tabData.accept(PraporModBlocks.CASKETOFSOULS.get().asItem());
				tabData.accept(PraporModItems.UPGRADE_INACTIVE.get());
				tabData.accept(PraporModItems.UPGRADE_ACTIVE.get());
				tabData.accept(PraporModItems.BETTER_MACE.get());
				tabData.accept(PraporModBlocks.GOLD_TROPHY.get().asItem());
			}).build());

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			tabData.accept(PraporModItems.PRAPOR_SPAWN_EGG.get());
			tabData.accept(PraporModItems.POOKER_SPAWN_EGG.get());
			tabData.accept(PraporModItems.SOUL_SPAWN_EGG.get());
			tabData.accept(PraporModItems.NARRATOR_SPAWN_EGG.get());
			tabData.accept(PraporModItems.BASTARD_SPAWN_EGG.get());
			tabData.accept(PraporModItems.BROLEM_SPAWN_EGG.get());
			tabData.accept(PraporModItems.DARKIRONKIN_SPAWN_EGG.get());
		}
	}
}
