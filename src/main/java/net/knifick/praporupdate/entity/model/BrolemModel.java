package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.init.PraporModTickets;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.entity.BrolemEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BrolemModel extends GeoModel<BrolemEntity> {
	@Override
	public ResourceLocation getAnimationResource(BrolemEntity entity) {
		return ResourceLocation.parse("prapor:animations/golem.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoRenderState entity) {
		return ResourceLocation.parse("prapor:geo/golem.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState state) {
		return ResourceLocation.parse("prapor:textures/entities/" + state.getGeckolibData(PraporModTickets.ENTITY_TEXTURE) + ".png");
	}
}
