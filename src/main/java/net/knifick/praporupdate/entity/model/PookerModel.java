package net.knifick.praporupdate.entity.model;

import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.entity.PookerEntity;

public class PookerModel extends GeoModel<PookerEntity> {
	@Override
	public ResourceLocation getAnimationResource(PookerEntity entity) {
		return ResourceLocation.parse("prapor:animations/pooker.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PookerEntity entity) {
		return ResourceLocation.parse("prapor:geo/pooker.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PookerEntity entity) {
		return ResourceLocation.parse("prapor:textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(PookerEntity animatable, long instanceId, AnimationState animationState) {
		GeoBone head = getAnimationProcessor().getBone("All");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
