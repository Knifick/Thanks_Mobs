
package net.knifick.praporupdate.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.knifick.praporupdate.entity.BastardEntity;
import net.knifick.praporupdate.entity.BobEntity;
import net.knifick.praporupdate.entity.SuckerEntity;
import net.knifick.praporupdate.entity.model.SuckerModel;
import net.knifick.praporupdate.init.PraporModTickets;
import net.knifick.praporupdate.procedures.PraporEntityVisualScaleProcedure;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.awt.*;

public class SuckerRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<SuckerEntity, R> {
	public SuckerRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new SuckerModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	@Nullable
	public RenderType getRenderType(R renderState, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

	@Override
	public void renderRecursively(R renderState, PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		if ("Eyes".equalsIgnoreCase(bone.getName()) || "Core".equalsIgnoreCase(bone.getName())) {
			super.renderRecursively(renderState, poseStack, bone, renderType, bufferSource, buffer,
					isReRender, packedLight, packedOverlay, 0xFFFFFFFF);
		} else {
			// Для остального — цвет сущности
			super.renderRecursively(renderState, poseStack, bone, renderType, bufferSource, buffer,
					isReRender, packedLight, packedOverlay, renderColor);
		}
	}

	@Override
	public void extractRenderState(SuckerEntity entity, R entityRenderState, float partialTick) {
		entityRenderState.addGeckolibData(PraporModTickets.ENTITY_TEXTURE, entity.getTexture());
		entityRenderState.addGeckolibData(PraporModTickets.ENTITY_COLOR, entity.getColor());
		entityRenderState.addGeckolibData(PraporModTickets.ENTITY_SUCK, entity.isSuck());
	}

	@Override
	public int getRenderColor(SuckerEntity animatable, Void relatedObject, float partialTick) {
		// super может уменьшить альфу (спектатор/невидимость и т.п.)
		int base = super.getRenderColor(animatable, relatedObject, partialTick);
		int a = ARGB.alpha(base);

		// твой цвет сущности как 0xRRGGBB (без альфы)
		int rgb = animatable.getColor() & 0x00FFFFFF;

		// вернём с альфой из super, а RGB — из сущности
		return (a << 24) | rgb;
	}
}
