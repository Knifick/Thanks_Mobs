package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.entity.NymphEntity;
import net.knifick.praporupdate.entity.PookerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class NymphModel extends GeoModel<NymphEntity> {
	@Override
	public ResourceLocation getAnimationResource(NymphEntity entity) {
		return ResourceLocation.parse("prapor:animations/nymph.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(NymphEntity entity) {
		return ResourceLocation.parse("prapor:geo/nymph.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(NymphEntity entity) {
		return ResourceLocation.parse("prapor:textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(NymphEntity animatable, long instanceId, AnimationState animationState) {
		GeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

		var tail1 = getAnimationProcessor().getBone("t1");
		var tail2 = getAnimationProcessor().getBone("t2");
		var tail3 = getAnimationProcessor().getBone("t3");
		var tail4 = getAnimationProcessor().getBone("t4");
		var tail5 = getAnimationProcessor().getBone("t5");

		float ageInTicks = animatable.tickCount + animationState.getPartialTick();
		float speed = (float) animatable.getDeltaMovement().length();
		float motionY = (float) animatable.getDeltaMovement().y;

		// Плавное наклонение хвоста вверх/вниз в зависимости от движения по Y
		float targetPitch = motionY * 0.5F; // амплитуда наклона, отрицательно = вниз
		float smoothFactor = 0.1F; // чем меньше, тем медленнее поворот
		// Для каждой кости создаем плавный переход
		if (tail1 != null) tail1.setRotX(tail1.getRotX() + (targetPitch - tail1.getRotX()) * smoothFactor);
		if (tail2 != null) tail2.setRotX(tail2.getRotX() + (targetPitch - tail2.getRotX()) * smoothFactor);
		if (tail3 != null) tail3.setRotX(tail3.getRotX() + (targetPitch - tail3.getRotX()) * smoothFactor);
		if (tail4 != null) tail4.setRotX(tail4.getRotX() + (targetPitch - tail4.getRotX()) * smoothFactor);
		if (tail5 != null) tail5.setRotX(tail5.getRotX() + (targetPitch - tail5.getRotX()) * smoothFactor);

		// Пассивное покачивание хвоста поверх наклона
		float baseAmplitude = 0.01F;
		float waveSpeed = 0.2F;
		if (tail1 != null) tail1.setRotX(tail1.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 1.0F) * baseAmplitude);
		if (tail2 != null) tail2.setRotX(tail2.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 1.4F) * baseAmplitude);
		if (tail3 != null) tail3.setRotX(tail3.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 1.9F) * baseAmplitude);
		if (tail4 != null) tail4.setRotX(tail4.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 2.4F) * baseAmplitude);
		if (tail5 != null) tail5.setRotX(tail5.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 2.8F) * baseAmplitude);
	}
}
