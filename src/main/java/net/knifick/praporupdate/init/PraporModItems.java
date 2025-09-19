/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.knifick.praporupdate.init;

import net.knifick.praporupdate.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.knifick.praporupdate.block.display.GoldTrophyDisplayItem;
import net.knifick.praporupdate.PraporMod;

import java.util.function.Function;

public class PraporModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(PraporMod.MODID);

	// --- Spawn eggs / bucket
	public static final DeferredItem<Item> PRAPOR_SPAWN_EGG =
			REGISTRY.registerItem("prapor_spawn_egg",
					props -> new CustomSpawnEggItem(PraporModEntities.PRAPOR.get(), props));

	public static final DeferredItem<Item> POOKER_SPAWN_EGG =
			REGISTRY.registerItem("pooker_spawn_egg",
					props -> new CustomSpawnEggItem(PraporModEntities.POOKER.get(), props));

	public static final DeferredItem<Item> NARRATOR_SPAWN_EGG =
			REGISTRY.registerItem("narrator_spawn_egg",
					props -> new CustomSpawnEggItem(PraporModEntities.NARRATOR.get(), props));

	public static final DeferredItem<Item> BASTARD_SPAWN_EGG =
			REGISTRY.registerItem("bastard_spawn_egg",
					props -> new CustomSpawnEggItem(PraporModEntities.BASTARD.get(), props));

	public static final DeferredItem<Item> BROLEM_SPAWN_EGG =
			REGISTRY.registerItem("brolem_spawn_egg",
					props -> new CustomSpawnEggItem(PraporModEntities.BROLEM.get(), props));

	public static final DeferredItem<Item> DARKIRONKIN_SPAWN_EGG =
			REGISTRY.registerItem("darkironkin_spawn_egg",
					props -> new CustomSpawnEggItem(PraporModEntities.DARKIRONKIN.get(), props));

	public static final DeferredItem<Item> SUCKER_SPAWN_EGG =
			REGISTRY.registerItem("sucker_spawn_egg",
					props -> new CustomSpawnEggItem(PraporModEntities.SUCKER.get(), props));

	public static final DeferredItem<Item> BOB_SPAWN_EGG =
			REGISTRY.registerItem("bob_spawn_egg",
					props -> new CustomSpawnEggItem(PraporModEntities.BOB.get(), props));

	public static final DeferredItem<Item> NYMPH_SPAWN_EGG =
			REGISTRY.registerItem("nymph_spawn_egg",
					props -> new CustomSpawnEggItem(PraporModEntities.NYMPH.get(), props));

	public static final DeferredItem<Item> MUCNHSAW_SPAWN_EGG =
			REGISTRY.registerItem("munchsaw_spawn_egg",
					props -> new CustomSpawnEggItem(PraporModEntities.MUNCHSAW.get(), props));

	public static final DeferredItem<Item> SOUL_SPAWN_EGG =
			REGISTRY.registerItem("soul_bucket",
					props -> new SoulBucket(PraporModEntities.SOUL.get(), props.stacksTo(1)));

	// --- Обычные айтемы (часть конструкторов без props — игнорим параметр)
	public static final DeferredItem<Item> PRAPORKA =
			REGISTRY.registerItem("praporka", props -> new PraporkaItem());

	public static final DeferredItem<Item> PRAPORKA_WITH_SIGN =
			REGISTRY.registerItem("praporka_with_sign", props -> new PraporkaWithSignItem());

	public static final DeferredItem<Item> SOUL_BOTTLE =
			REGISTRY.registerItem("soul_bottle", props -> new SoulBottleItem());

	public static final DeferredItem<Item> SPAWNER_SHARD =
			REGISTRY.registerItem("spawner_shard", props -> new SpawnerShardItem());

	public static final DeferredItem<Item> MUSIC_RECORD_N_42 =
			REGISTRY.registerItem("music_record_n_42", props -> new MusicRecordN42Item());

	public static final DeferredItem<Item> BATTERY =
			REGISTRY.registerItem("battery", props -> new BatteryItem());

	public static final DeferredItem<Item> COPPER_WIRE =
			REGISTRY.registerItem("copper_wire", props -> new CopperWireItem());

	public static final DeferredItem<Item> BOARD =
			REGISTRY.registerItem("board", props -> new BoardItem());

	public static final DeferredItem<Item> OXIDIZED_COPPER_INGOT =
			REGISTRY.registerItem("oxidized_copper_ingot", props -> new OxidizedCopperIngotItem());

	public static final DeferredItem<Item> OXIDIZEDCOPPERSHEET =
			REGISTRY.registerItem("oxidizedcoppersheet", props -> new OxidizedcoppersheetItem());

	public static final DeferredItem<Item> MUSIC_RECORD_THANKS_STREET =
			REGISTRY.registerItem("music_record_thanks_street", props -> new MusicRecordThanksStreetItem());

	public static final DeferredItem<Item> HOTDOG =
			REGISTRY.registerItem("hotdog", props -> new HotDogItem());

	public static final DeferredItem<Item> GUIDE_BOOK =
			REGISTRY.registerItem("guide_book", props -> new GuideBookItem());

	public static final DeferredItem<Item> CAVIAR =
			REGISTRY.registerItem("caviar", props -> new CaviarItem());

	// --- Композитные/сложные айтемы с модификацией props
	public static final DeferredItem<Item> BETTER_MACE =
			REGISTRY.registerItem("dark_mace",
					props -> new BetterMaceItem(
							props
									.rarity(Rarity.EPIC)
									.durability(500)
									.component(DataComponents.TOOL, BetterMaceChargedItem.createToolProperties())
									.attributes(BetterMaceChargedItem.createAttributes())
					));

	public static final DeferredItem<Item> BETTER_MACE_CHARGED =
			REGISTRY.registerItem("dark_mace_charged", props -> new BetterMaceChargedItem());

	public static final DeferredItem<Item> UPGRADE_ACTIVE =
			REGISTRY.registerItem("upgrade_active",
					props -> new Item(props.rarity(Rarity.EPIC).fireResistant()));

	public static final DeferredItem<Item> UPGRADE_INACTIVE =
			REGISTRY.registerItem("upgrade_inactive",
					props -> new Item(props.rarity(Rarity.EPIC).fireResistant()));

	public static final DeferredItem<Item> MANTLE =
			REGISTRY.registerItem("mantle",
					props -> new MantleItem(ArmorMaterials.IRON, ArmorType.CHESTPLATE,
							props.durability(10).rarity(Rarity.EPIC)));

	public static final DeferredItem<Item> HAT_HELMET =
			register("hat_helmet", p -> new HatItem.Helmet(p));

	// --- BlockItems
	public static final DeferredItem<Item> CASKETOFSOULS =
			block(PraporModBlocks.CASKETOFSOULS);

	public static final DeferredItem<Item> GOLD_TROPHY =
			REGISTRY.registerItem(PraporModBlocks.GOLD_TROPHY.getId().getPath(),
					props -> new GoldTrophyDisplayItem(PraporModBlocks.GOLD_TROPHY.get(), props));

	// ----------------------------------------------------------------

	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		// критично: именно registerItem, и прокидываем props в BlockItem
		return REGISTRY.registerItem(block.getId().getPath(),
				props -> new BlockItem(block.get(), props));
	}

	// общий хелпер, чтобы не повторяться
	private static <I extends Item> DeferredItem<I> register(String name, Function<Item.Properties, ? extends I> supplier) {
		return REGISTRY.registerItem(name, supplier, new Item.Properties());
	}
}
