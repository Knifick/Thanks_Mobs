
package net.knifick.praporupdate.item;

import net.knifick.praporupdate.init.PraporModMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CaviarItem extends Item {
	public CaviarItem() {
		super(new Properties().food((new FoodProperties.Builder()).nutrition(4).saturationModifier(0.3f).alwaysEdible().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return 40;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
		entity.addEffect(new MobEffectInstance(PraporModMobEffects.PURGE, 1200, 0));
		return super.finishUsingItem(itemStack, level, entity);
	}
}
