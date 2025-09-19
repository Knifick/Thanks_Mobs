package net.knifick.praporupdate.entity.model;

import net.knifick.praporupdate.entity.BastardEntity;
import net.knifick.praporupdate.entity.SuckerEntity;
import net.knifick.praporupdate.init.PraporModTickets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Objects;

public class SuckerModel extends GeoModel<SuckerEntity> {
	@Override
	public ResourceLocation getAnimationResource(SuckerEntity entity) {
		return ResourceLocation.parse("prapor:animations/sucker.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoRenderState entity) {
		return ResourceLocation.parse("prapor:geo/sucker.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoRenderState entity) {
		String base = "prapor:textures/entities/sucker/";
		String texture = entity.getGeckolibData(PraporModTickets.ENTITY_TEXTURE); // базовое имя текстуры

		String suffix;

		if(entity.getGeckolibData(PraporModTickets.ENTITY_COLOR) == -1) {
			suffix = "_colored.png";
		} else {
			suffix = ".png";
		}

		// если сущность не сосёт, вставляем _unsuck перед _colored
		if (Boolean.FALSE.equals(entity.getGeckolibData(PraporModTickets.ENTITY_SUCK)) && suffix.equals("_colored.png")) {
			suffix = "_unsuck_colored.png";
		} else if (Boolean.FALSE.equals(entity.getGeckolibData(PraporModTickets.ENTITY_SUCK)) && suffix.equals(".png")) {
			suffix = "_unsuck.png";
		}

		return ResourceLocation.parse(base + texture + suffix);
	}

	@Override
	public void setCustomAnimations(AnimationState<SuckerEntity> animationState) {
		super.setCustomAnimations(animationState);
		GeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			Float pitch = animationState.getData(DataTickets.ENTITY_PITCH);
			Float yaw   = animationState.getData(DataTickets.ENTITY_YAW);

			if (pitch != null) head.setRotX(pitch * Mth.DEG_TO_RAD);
			if (yaw   != null) head.setRotY(yaw * Mth.DEG_TO_RAD);
		}
		// Получаем кость через Optional
		this.getBone("RSheka").ifPresent(tailBone -> {
			// Управление видимостью на основе данных сущности
            tailBone.setHidden(!Objects.equals(animationState.renderState().getGeckolibData(PraporModTickets.ENTITY_TEXTURE), "sucker_suck"));
		});
		this.getBone("LSheka").ifPresent(tailBone -> {
			// Управление видимостью на основе данных сущности
			tailBone.setHidden(!Objects.equals(animationState.renderState().getGeckolibData(PraporModTickets.ENTITY_TEXTURE), "sucker_suck"));
		});
	}
}
