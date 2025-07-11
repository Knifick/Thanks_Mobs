
package net.knifick.praporupdate.potion;

import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.EffectCure;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.PraporMod;

import java.util.Set;

public class FearMobEffect extends InstantenousMobEffect {
	public FearMobEffect() {
		super(MobEffectCategory.HARMFUL, -15128278);
		this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath(PraporMod.MODID, "effect.fear_0"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		this.addAttributeModifier(Attributes.ATTACK_KNOCKBACK, ResourceLocation.fromNamespaceAndPath(PraporMod.MODID, "effect.fear_1"), 0.7, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
		cures.add(EffectCures.MILK);
		cures.add(EffectCures.HONEY);
	}
}
