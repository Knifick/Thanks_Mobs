
package net.knifick.praporupdate.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.knifick.praporupdate.entity.model.DarkironkinModel;
import net.knifick.praporupdate.entity.DarkironkinEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class DarkironkinRenderer extends GeoEntityRenderer<DarkironkinEntity> {
	public DarkironkinRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new DarkironkinModel());
		this.shadowRadius = 1.3f;
	}

	@Override
	public RenderType getRenderType(DarkironkinEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, DarkironkinEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(DarkironkinEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
