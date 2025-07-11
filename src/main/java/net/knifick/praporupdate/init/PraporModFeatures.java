
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.knifick.praporupdate.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.core.registries.Registries;

import net.knifick.praporupdate.world.features.NetherFeatherFeature;
import net.knifick.praporupdate.PraporMod;

public class PraporModFeatures {
	public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(Registries.FEATURE, PraporMod.MODID);
	public static final DeferredHolder<Feature<?>, Feature<?>> NETHER_FEATHER = REGISTRY.register("nether_feather", NetherFeatherFeature::new);
}
