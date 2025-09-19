
package net.knifick.praporupdate.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class BatteryItem extends Item {
	public BatteryItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));
	}
}
