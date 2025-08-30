
package net.knifick.praporupdate.potion;

import net.knifick.praporupdate.PraporMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;

import java.util.Set;

public class PurgeMobEffect extends InstantenousMobEffect {
	public PurgeMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -8339457);
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
		cures.add(EffectCures.MILK);
		cures.add(EffectCures.HONEY);
	}
}
