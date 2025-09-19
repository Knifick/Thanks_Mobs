package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.init.PraporModTickets;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.entity.NarratorEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class NarratorModel extends GeoModel<NarratorEntity> {
	@Override
	public ResourceLocation getAnimationResource(NarratorEntity entity) {
		return ResourceLocation.parse("prapor:animations/narrator.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoRenderState entity) {
		return ResourceLocation.parse("prapor:geo/narrator.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState state) {
		return ResourceLocation.parse("prapor:textures/entities/" + state.getGeckolibData(PraporModTickets.ENTITY_TEXTURE) + ".png");
	}

	@Override
	public void setCustomAnimations(AnimationState<NarratorEntity> animationState) {
		super.setCustomAnimations(animationState);

		// Получаем кость через Optional
		this.getBone("Plata").ifPresent(tailBone -> {
			// Управление видимостью на основе данных сущности
			tailBone.setHidden(Boolean.FALSE.equals(animationState.renderState().getGeckolibData(PraporModTickets.ENTITY_TAME)));
		});
	}
}
