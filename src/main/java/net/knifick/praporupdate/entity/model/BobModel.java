package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.entity.BobEntity;
import net.knifick.praporupdate.entity.PookerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BobModel extends GeoModel<BobEntity> {
	@Override
	public ResourceLocation getAnimationResource(BobEntity entity) {
		return ResourceLocation.parse("prapor:animations/bob.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BobEntity entity) {
		return ResourceLocation.parse("prapor:geo/bob.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BobEntity entity) {
		return ResourceLocation.parse("prapor:textures/entities/" + entity.getTexture() + ".png");
	}
}
