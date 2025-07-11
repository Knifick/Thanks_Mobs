package net.knifick.praporupdate.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.entity.BrolemEntity;

public class BrolemModel extends GeoModel<BrolemEntity> {
	@Override
	public ResourceLocation getAnimationResource(BrolemEntity entity) {
		return ResourceLocation.parse("prapor:animations/golem.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BrolemEntity entity) {
		return ResourceLocation.parse("prapor:geo/golem.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BrolemEntity entity) {
		return ResourceLocation.parse("prapor:textures/entities/druzhochek.png");
	}

}
