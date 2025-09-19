package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.entity.BastardEntity;
import net.knifick.praporupdate.init.PraporModTickets;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.entity.PookerEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class PookerModel extends GeoModel<PookerEntity> {
	@Override
	public ResourceLocation getAnimationResource(PookerEntity entity) {
		return ResourceLocation.parse("prapor:animations/pooker.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoRenderState entity) {
		return ResourceLocation.parse("prapor:geo/pooker.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState state) {
		return ResourceLocation.parse("prapor:textures/entities/" + state.getGeckolibData(PraporModTickets.ENTITY_TEXTURE) + ".png");
	}

	@Override
	public void setCustomAnimations(AnimationState<PookerEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			Float pitch = animationState.getData(DataTickets.ENTITY_PITCH);
			Float yaw   = animationState.getData(DataTickets.ENTITY_YAW);

			if (pitch != null) head.setRotX(pitch * Mth.DEG_TO_RAD);
			if (yaw   != null) head.setRotY(yaw * Mth.DEG_TO_RAD);
		}
	}
}
