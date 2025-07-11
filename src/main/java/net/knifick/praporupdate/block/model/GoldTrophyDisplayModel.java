package net.knifick.praporupdate.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.block.display.GoldTrophyDisplayItem;

public class GoldTrophyDisplayModel extends GeoModel<GoldTrophyDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(GoldTrophyDisplayItem animatable) {
		return ResourceLocation.parse("prapor:animations/gold_wither.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GoldTrophyDisplayItem animatable) {
		return ResourceLocation.parse("prapor:geo/gold_wither.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GoldTrophyDisplayItem entity) {
		return ResourceLocation.parse("prapor:textures/block/gold_statue.png");
	}
}
