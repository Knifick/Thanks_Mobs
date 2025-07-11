
package net.knifick.praporupdate.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class SpawnerShardItem extends Item {
	public SpawnerShardItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
	}
}
