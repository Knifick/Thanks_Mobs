package net.knifick.praporupdate.item.model;

import net.knifick.praporupdate.entity.NymphEntity;
import net.knifick.praporupdate.item.MantleItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MantleModel extends GeoModel<MantleItem> {
	@Override
	public ResourceLocation getAnimationResource(MantleItem object) {
		return ResourceLocation.parse("prapor:animations/mantle.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MantleItem object) {
		return ResourceLocation.parse("prapor:geo/mantle.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MantleItem object) {
		return ResourceLocation.parse("prapor:textures/armor/mantle_armor.png");
	}

	@Override
	public void setCustomAnimations(MantleItem animatable, long instanceId, AnimationState animationState) {
		LivingEntity wearer = (LivingEntity) animationState.getData(DataTickets.ENTITY);
		if (wearer == null) return;

		// Получаем кости хвоста
		GeoBone m1 = getAnimationProcessor().getBone("m1");
		GeoBone m2 = getAnimationProcessor().getBone("m2");
		GeoBone m3 = getAnimationProcessor().getBone("m3");
		GeoBone m4 = getAnimationProcessor().getBone("m4");

		float ageInTicks = wearer.tickCount + animationState.getPartialTick();
		float speed = (float) wearer.getDeltaMovement().length();
		float motionY = (float) wearer.getDeltaMovement().y;

		float targetPitch = motionY * 1.3F;

		float maxUp = -0.1F;   // вверх
		float maxDown = -0.6F; // вниз
		targetPitch = Mth.clamp(targetPitch, maxDown, maxUp);
		float smoothFactor = 0.1F; // чем меньше, тем плавнее

		if (m1 != null) m1.setRotX(m1.getRotX() + (targetPitch - m1.getRotX()*0.35f) * smoothFactor);
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
