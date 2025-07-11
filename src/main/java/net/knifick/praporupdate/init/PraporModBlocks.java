
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.knifick.praporupdate.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.Block;

import net.knifick.praporupdate.block.GoldTrophyBlock;
import net.knifick.praporupdate.block.CasketofsoulsBlock;
import net.knifick.praporupdate.PraporMod;

public class PraporModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(PraporMod.MODID);
	public static final DeferredBlock<Block> CASKETOFSOULS = REGISTRY.register("casketofsouls", CasketofsoulsBlock::new);
	public static final DeferredBlock<Block> GOLD_TROPHY = REGISTRY.register("gold_trophy", GoldTrophyBlock::new);
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
