package net.knifick.praporupdate.item.model;

import net.knifick.praporupdate.item.MantleItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MantleModel extends GeoModel<MantleItem> {
	@Override
	public ResourceLocation getAnimationResource(MantleItem state) {
		return ResourceLocation.fromNamespaceAndPath("prapor", "animations/mantle.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoRenderState state) {
		return ResourceLocation.fromNamespaceAndPath("prapor", "geo/mantle.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState state) {
		return ResourceLocation.fromNamespaceAndPath("prapor", "textures/armor/mantle_armor.png");
	}

	@Override
	public void setCustomAnimations(AnimationState<MantleItem> animationState) {
		// Получаем данные из DataTickets
		Double tick = animationState.getData(DataTickets.TICK);
		Float partial = animationState.getData(DataTickets.PARTIAL_TICK);
		Vec3 velocity = animationState.getData(DataTickets.VELOCITY);

		if (tick == null || partial == null || velocity == null)
			return;

		// Кости
		GeoBone m1 = getAnimationProcessor().getBone("m1");
		GeoBone m2 = getAnimationProcessor().getBone("m2");
		GeoBone m3 = getAnimationProcessor().getBone("m3");
		GeoBone m4 = getAnimationProcessor().getBone("m4");

		// Возраст + дельта
		float ageInTicks = tick.floatValue() + partial;
		float motionY = (float) velocity.y;

		// Целевой угол
		float targetPitch = motionY * 1.3F;
		targetPitch = Mth.clamp(targetPitch, -0.6F, -0.1F); // ограничиваем

		float smoothFactor = 0.1F;

		if (m1 != null) m1.setRotX(m1.getRotX() + (targetPitch - m1.getRotX() * 0.35f) * smoothFactor);
		if (m2 != null) m2.setRotX(m2.getRotX() + (targetPitch - m2.getRotX()) * smoothFactor);
		if (m3 != null) m3.setRotX(m3.getRotX() + (targetPitch - m3.getRotX()) * smoothFactor);
		if (m4 != null) m4.setRotX(m4.getRotX() + (targetPitch - m4.getRotX()) * smoothFactor);

		// Пассивное покачивание
		float baseAmplitude = 0.005F;
		float waveSpeed = 0.1F;
		if (m1 != null) m1.setRotX(m1.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 1.0F) * baseAmplitude);
		if (m2 != null) m2.setRotX(m2.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 1.4F) * baseAmplitude);
		if (m3 != null) m3.setRotX(m3.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 1.9F) * baseAmplitude);
		if (m4 != null) m4.setRotX(m4.getRotX() + (float) Math.sin(ageInTicks * waveSpeed + 2.4F) * baseAmplitude);
	}

}
