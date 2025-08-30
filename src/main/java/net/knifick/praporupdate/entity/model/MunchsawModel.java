package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.entity.MunchsawEntity;
import net.knifick.praporupdate.entity.PraporEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MunchsawModel extends GeoModel<MunchsawEntity> {
	@Override
	public ResourceLocation getAnimationResource(MunchsawEntity entity) {
		return ResourceLocation.parse("prapor:animations/munchsaw.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MunchsawEntity entity) {
		return ResourceLocation.parse("prapor:geo/munchsaw.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MunchsawEntity entity) {
		return ResourceLocation.parse("prapor:textures/entities/" + entity.getTexture() + ".png");
	}

}
