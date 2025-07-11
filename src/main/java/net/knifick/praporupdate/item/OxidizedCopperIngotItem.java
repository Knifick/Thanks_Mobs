
package net.knifick.praporupdate.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class OxidizedCopperIngotItem extends Item {
	public OxidizedCopperIngotItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}
}
