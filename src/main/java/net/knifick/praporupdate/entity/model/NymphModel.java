package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.entity.BastardEntity;
import net.knifick.praporupdate.entity.NymphEntity;
import net.knifick.praporupdate.entity.PookerEntity;
import net.knifick.praporupdate.init.PraporModTickets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class NymphModel extends GeoModel<NymphEntity> {
	@Override
	public ResourceLocation getAnimationResource(NymphEntity entity) {
		return ResourceLocation.parse("prapor:animations/nymph.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoRenderState entity) {
		return ResourceLocation.parse("prapor:geo/nymph.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState state) {
		return ResourceLocation.parse("prapor:textures/entities/" + state.getGeckolibData(PraporModTickets.ENTITY_TEXTURE) + ".png");
	}

	@Override
	public void setCustomAnimations(AnimationState<NymphEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			Float pitch = animationState.getData(DataTickets.ENTITY_PITCH);
			Float yaw   = animationState.getData(DataTickets.ENTITY_YAW);

			if (pitch != null) head.setRotX(pitch * Mth.DEG_TO_RAD);
			if (yaw   != null) head.setRotY(yaw * Mth.DEG_TO_RAD);
		}

		GeoBone tail1 = getAnimationProcessor().getBone("t1");
		GeoBone tail2 = getAnimationProcessor().getBone("t2");
		GeoBone tail3 = getAnimationProcessor().getBone("t3");
		GeoBone tail4 = getAnimationProcessor().getBone("t4");
		GeoBone tail5 = getAnimationProcessor().getBone("t5");

		// получаем данные из DataTickets
		@Nullable Double ageInTicks = animationState.getData(DataTickets.ANIMATION_TICKS);
		var velocity = animationState.getData(DataTickets.VELOCITY);

		if (ageInTicks == null) ageInTicks = 0.0;

		float motionY = velocity != null ? (float) velocity.y : 0f;

		// Плавное наклонение хвоста
		float targetPitch = motionY * 0.5F;
		float smoothFactor = 0.1F;

		if (tail1 != null) tail1.setRotX(tail1.getRotX() + (targetPitch - tail1.getRotX()) * smoothFactor);
		if (tail2 != null) tail2.setRotX(tail2.getRotX() + (targetPitch - tail2.getRotX()) * smoothFactor);
		if (tail3 != null) tail3.setRotX(tail3.getRotX() + (targetPitch - tail3.getRotX()) * smoothFactor);
		if (tail4 != null) tail4.setRotX(tail4.getRotX() + (targetPitch - tail4.getRotX()) * smoothFactor);
		if (tail5 != null) tail5.setRotX(tail5.getRotX() + (targetPitch - tail5.getRotX()) * smoothFactor);

		// Пассивное покачивание
		float baseAmplitude = 0.01F;
		float waveSpeed = 0.2F;

		if (tail1 != null) tail1.setRotX(tail1.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 1.0F) * baseAmplitude);
		if (tail2 != null) tail2.setRotX(tail2.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 1.4F) * baseAmplitude);
		if (tail3 != null) tail3.setRotX(tail3.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 1.9F) * baseAmplitude);
		if (tail4 != null) tail4.setRotX(tail4.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 2.4F) * baseAmplitude);
		if (tail5 != null) tail5.setRotX(tail5.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 2.8F) * baseAmplitude);
	}

}
