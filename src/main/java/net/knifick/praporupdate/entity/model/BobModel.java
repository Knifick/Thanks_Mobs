package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.entity.BobEntity;
import net.knifick.praporupdate.init.PraporModTickets;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BobModel extends GeoModel<BobEntity> {
	@Override
	public ResourceLocation getAnimationResource(BobEntity entity) {
		return ResourceLocation.parse("prapor:animations/bob.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoRenderState entity) {
		return ResourceLocation.parse("prapor:geo/bob.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState state) {
		return ResourceLocation.parse("prapor:textures/entities/" + state.getGeckolibData(PraporModTickets.ENTITY_TEXTURE) + ".png");
	}
}
