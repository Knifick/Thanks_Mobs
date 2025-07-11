
package net.knifick.praporupdate.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.knifick.praporupdate.procedures.PraporEntityVisualScaleProcedure;
import net.knifick.praporupdate.entity.model.PraporModel;
import net.knifick.praporupdate.entity.PraporEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class PraporRenderer extends GeoEntityRenderer<PraporEntity> {
	public PraporRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new PraporModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	public RenderType getRenderType(PraporEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, PraporEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		float scale = (float) PraporEntityVisualScaleProcedure.execute(entity);
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(PraporEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
