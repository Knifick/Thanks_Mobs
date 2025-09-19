package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.entity.BastardEntity;
import net.knifick.praporupdate.init.PraporModTickets;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

import net.knifick.praporupdate.entity.DarkironkinEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DarkironkinModel extends GeoModel<DarkironkinEntity> {
	@Override
	public ResourceLocation getAnimationResource(DarkironkinEntity entity) {
		return ResourceLocation.parse("prapor:animations/dark_ironkin.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoRenderState entity) {
		ResourceLocation path = ResourceLocation.parse("prapor:geo/dark_ironkin.geo.json");
		String name = entity.getOrDefaultGeckolibData(PraporModTickets.ENTITY_NAME, "");
		if(name!=null){
			if(name.equalsIgnoreCase("Mega knight"))
				path = ResourceLocation.parse("prapor:geo/dark_ironkin_mega.geo.json");
		}
		return path;
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState entity) {
		ResourceLocation path = ResourceLocation.parse("prapor:textures/entities/dark_ironkin.png");
		String name = entity.getOrDefaultGeckolibData(PraporModTickets.ENTITY_NAME, "");
		if(name!=null) {
			if (name.equalsIgnoreCase("Mega knight"))
				path = ResourceLocation.parse("prapor:textures/entities/megagay.png");
		}
		return path;
	}

	@Override
	public void setCustomAnimations(AnimationState<DarkironkinEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			Float pitch = animationState.getData(DataTickets.ENTITY_PITCH);
			Float yaw   = animationState.getData(DataTickets.ENTITY_YAW);

			if (pitch != null) head.setRotX(pitch * Mth.DEG_TO_RAD);
			if (yaw   != null) head.setRotY(yaw * Mth.DEG_TO_RAD);
		}
	}
}
