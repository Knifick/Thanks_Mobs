
package net.knifick.praporupdate.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class BatteryItem extends Item {
	public BatteryItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));
	}
}
