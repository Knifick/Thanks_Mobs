
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.knifick.praporupdate.init;

import net.knifick.praporupdate.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.knifick.praporupdate.block.display.GoldTrophyDisplayItem;
import net.knifick.praporupdate.PraporMod;

public class PraporModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(PraporMod.MODID);
	public static final DeferredItem<Item> PRAPOR_SPAWN_EGG = REGISTRY.register("prapor_spawn_egg", () -> new DeferredSpawnEggItem(PraporModEntities.PRAPOR, -1920443, -1019607, new Item.Properties()));
	public static final DeferredItem<Item> PRAPORKA = REGISTRY.register("praporka", PraporkaItem::new);
	public static final DeferredItem<Item> PRAPORKA_WITH_SIGN = REGISTRY.register("praporka_with_sign", PraporkaWithSignItem::new);
	public static final DeferredItem<Item> POOKER_SPAWN_EGG = REGISTRY.register("pooker_spawn_egg", () -> new DeferredSpawnEggItem(PraporModEntities.POOKER, -15655332, -12959625, new Item.Properties()));
	public static final DeferredItem<Item> SOUL_SPAWN_EGG = REGISTRY.register("soul_spawn_egg", () -> new DeferredSpawnEggItem(PraporModEntities.SOUL, -10167824, -7046051, new Item.Properties()));
	public static final DeferredItem<Item> NARRATOR_SPAWN_EGG = REGISTRY.register("narrator_spawn_egg", () -> new DeferredSpawnEggItem(PraporModEntities.NARRATOR, -13423063, -13072858, new Item.Properties()));
	public static final DeferredItem<Item> BASTARD_SPAWN_EGG = REGISTRY.register("bastard_spawn_egg", () -> new DeferredSpawnEggItem(PraporModEntities.BASTARD, -2469086, -3630515, new Item.Properties()));
	public static final DeferredItem<Item> SOUL_BOTTLE = REGISTRY.register("soul_bottle", SoulBottleItem::new);
	public static final DeferredItem<Item> CASKETOFSOULS = block(PraporModBlocks.CASKETOFSOULS);
	public static final DeferredItem<Item> SPAWNER_SHARD = REGISTRY.register("spawner_shard", SpawnerShardItem::new);
	public static final DeferredItem<Item> BROLEM_SPAWN_EGG = REGISTRY.register("brolem_spawn_egg", () -> new DeferredSpawnEggItem(PraporModEntities.BROLEM, -12500671, -11699287, new Item.Properties()));
	public static final DeferredItem<Item> MUSIC_RECORD_N_42 = REGISTRY.register("music_record_n_42", MusicRecordN42Item::new);
	public static final DeferredItem<Item> DARKIRONKIN_SPAWN_EGG = REGISTRY.register("darkironkin_spawn_egg", () -> new DeferredSpawnEggItem(PraporModEntities.DARKIRONKIN, -14935012, -4512218, new Item.Properties()));
	public static final DeferredItem<Item> BATTERY = REGISTRY.register("battery", BatteryItem::new);
	public static final DeferredItem<Item> COPPER_WIRE = REGISTRY.register("copper_wire", CopperWireItem::new);
	public static final DeferredItem<Item> BOARD = REGISTRY.register("board", BoardItem::new);
	public static final DeferredItem<Item> GOLD_TROPHY = REGISTRY.register(PraporModBlocks.GOLD_TROPHY.getId().getPath(), () -> new GoldTrophyDisplayItem(PraporModBlocks.GOLD_TROPHY.get(), new Item.Properties()));
	public static final DeferredItem<Item> OXIDIZED_COPPER_INGOT = REGISTRY.register("oxidized_copper_ingot", OxidizedCopperIngotItem::new);
	public static final DeferredItem<Item> OXIDIZEDCOPPERSHEET = REGISTRY.register("oxidizedcoppersheet", OxidizedcoppersheetItem::new);
	public static final DeferredItem<Item> MUSIC_RECORD_THANKS_STREET = REGISTRY.register("music_record_thanks_street", MusicRecordThanksStreetItem::new);
	public static final DeferredItem<Item> HAT_HELMET = REGISTRY.register("hat_helmet", HatItem.Helmet::new);
	public static final DeferredItem<Item> BETTER_MACE = REGISTRY.register("dark_mace", () -> new BetterMaceItem());
	public static final DeferredItem<Item> BETTER_MACE_CHARGED = REGISTRY.register("dark_mace_charged", BetterMaceChargedItem::new);
	public static final DeferredItem<Item> UPGRADE_ACTIVE = REGISTRY.register("upgrade_active", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
	public static final DeferredItem<Item> UPGRADE_INACTIVE = REGISTRY.register("upgrade_inactive", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant()));

	// Start of user code block custom items
	// End of user code block custom items
	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
