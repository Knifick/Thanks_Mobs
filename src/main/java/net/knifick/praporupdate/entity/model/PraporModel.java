package net.knifick.praporupdate.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.entity.PraporEntity;

public class PraporModel extends GeoModel<PraporEntity> {
	@Override
	public ResourceLocation getAnimationResource(PraporEntity entity) {
		return ResourceLocation.parse("prapor:animations/prapor.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PraporEntity entity) {
		return ResourceLocation.parse("prapor:geo/prapor.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PraporEntity entity) {
		return ResourceLocation.parse("prapor:textures/entities/" + entity.getTexture() + ".png");
	}

}
