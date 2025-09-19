package net.knifick.praporupdate.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.block.entity.GoldTrophyTileEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GoldTrophyBlockModel extends GeoModel<GoldTrophyTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(GoldTrophyTileEntity animatable) {
		return ResourceLocation.parse("prapor:animations/gold_wither.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoRenderState animatable) {
		return ResourceLocation.parse("prapor:geo/gold_wither.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState animatable) {
		return ResourceLocation.parse("prapor:textures/block/gold_statue.png");
	}
}
