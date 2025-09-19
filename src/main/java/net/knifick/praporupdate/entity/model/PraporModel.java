package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.init.PraporModTickets;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.entity.PraporEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class PraporModel extends GeoModel<PraporEntity> {
	@Override
	public ResourceLocation getAnimationResource(PraporEntity entity) {
		return ResourceLocation.parse("prapor:animations/prapor.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoRenderState entity) {
		return ResourceLocation.parse("prapor:geo/prapor.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState state) {
		return ResourceLocation.parse("prapor:textures/entities/" + state.getGeckolibData(PraporModTickets.ENTITY_TEXTURE) + ".png");
	}
}
