package net.knifick.praporupdate.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.block.entity.GoldTrophyTileEntity;

public class GoldTrophyBlockModel extends GeoModel<GoldTrophyTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(GoldTrophyTileEntity animatable) {
		return ResourceLocation.parse("prapor:animations/gold_wither.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GoldTrophyTileEntity animatable) {
		return ResourceLocation.parse("prapor:geo/gold_wither.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GoldTrophyTileEntity animatable) {
		return ResourceLocation.parse("prapor:textures/block/gold_statue.png");
	}
}
