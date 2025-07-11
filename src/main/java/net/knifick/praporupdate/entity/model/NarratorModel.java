package net.knifick.praporupdate.entity.model;

import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.entity.NarratorEntity;

public class NarratorModel extends GeoModel<NarratorEntity> {
	@Override
	public ResourceLocation getAnimationResource(NarratorEntity entity) {
		return ResourceLocation.parse("prapor:animations/narrator.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(NarratorEntity entity) {
		return ResourceLocation.parse("prapor:geo/narrator.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(NarratorEntity entity) {
		return ResourceLocation.parse("prapor:textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(NarratorEntity animatable, long instanceId, AnimationState<NarratorEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);

		// Получаем кость через Optional
		this.getBone("Plata").ifPresent(tailBone -> {
			// Управление видимостью на основе данных сущности
			tailBone.setHidden(!animatable.isTame());
		});
	}
}
