
package net.knifick.praporupdate.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class SpawnerShardItem extends Item {
	public SpawnerShardItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
	}
}
