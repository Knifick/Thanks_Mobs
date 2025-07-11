
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.knifick.praporupdate.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import net.knifick.praporupdate.PraporMod;

public class PraporModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, PraporMod.MODID);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARKS = REGISTRY.register("sparks", () -> new SimpleParticleType(false));
}
