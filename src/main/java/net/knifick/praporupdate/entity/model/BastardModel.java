package net.knifick.praporupdate.entity.model;

import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.entity.BastardEntity;

public class BastardModel extends GeoModel<BastardEntity> {
	@Override
	public ResourceLocation getAnimationResource(BastardEntity entity) {
		return ResourceLocation.parse("prapor:animations/padla.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BastardEntity entity) {
		return ResourceLocation.parse("prapor:geo/padla.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BastardEntity entity) {
		return ResourceLocation.parse("prapor:textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(BastardEntity animatable, long instanceId, AnimationState animationState) {
		GeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
