
package net.knifick.praporupdate.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

import net.knifick.praporupdate.PraporMod;

public class MusicRecordThanksStreetItem extends Item {
	public MusicRecordThanksStreetItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(PraporMod.MODID, "music_record_thanks_street"))));
	}
}
