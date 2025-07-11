
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.knifick.praporupdate.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.Registries;

import net.knifick.praporupdate.potion.FearMobEffect;
import net.knifick.praporupdate.PraporMod;

public class PraporModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, PraporMod.MODID);
	public static final DeferredHolder<MobEffect, MobEffect> FEAR = REGISTRY.register("fear", () -> new FearMobEffect());
}
