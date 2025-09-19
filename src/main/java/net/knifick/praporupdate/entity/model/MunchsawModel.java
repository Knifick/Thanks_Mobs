package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.entity.MunchsawEntity;
import net.knifick.praporupdate.entity.PraporEntity;
import net.knifick.praporupdate.init.PraporModTickets;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MunchsawModel extends GeoModel<MunchsawEntity> {
	@Override
	public ResourceLocation getAnimationResource(MunchsawEntity entity) {
		return ResourceLocation.parse("prapor:animations/munchsaw.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoRenderState entity) {
		return ResourceLocation.parse("prapor:geo/munchsaw.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState state) {
		return ResourceLocation.parse("prapor:textures/entities/" + state.getGeckolibData(PraporModTickets.ENTITY_TEXTURE) + ".png");
	}
}
