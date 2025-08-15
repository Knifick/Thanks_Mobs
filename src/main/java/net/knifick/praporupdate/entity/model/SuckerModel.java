package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.entity.NarratorEntity;
import net.knifick.praporupdate.entity.PraporEntity;
import net.knifick.praporupdate.entity.SuckerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import java.util.Objects;

public class SuckerModel extends GeoModel<SuckerEntity> {
	@Override
	public ResourceLocation getAnimationResource(SuckerEntity entity) {
		return ResourceLocation.parse("prapor:animations/sucker.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SuckerEntity entity) {
		return ResourceLocation.parse("prapor:geo/sucker.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SuckerEntity entity) {
		return ResourceLocation.parse("prapor:textures/entities/sucker/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(SuckerEntity animatable, long instanceId, AnimationState<SuckerEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);

		// Получаем кость через Optional
		this.getBone("RSheka").ifPresent(tailBone -> {
			// Управление видимостью на основе данных сущности
            tailBone.setHidden(!Objects.equals(animatable.getTexture(), "sucker_suck"));
		});
		this.getBone("LSheka").ifPresent(tailBone -> {
			// Управление видимостью на основе данных сущности
			tailBone.setHidden(!Objects.equals(animatable.getTexture(), "sucker_suck"));
		});
	}
}
