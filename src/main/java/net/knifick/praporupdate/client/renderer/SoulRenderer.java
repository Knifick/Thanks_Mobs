
package net.knifick.praporupdate.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.Minecraft;

import net.knifick.praporupdate.entity.SoulEntity;
import net.knifick.praporupdate.client.model.ModelCustomModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class SoulRenderer extends MobRenderer<SoulEntity, ModelCustomModel<SoulEntity>> {
	public SoulRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelCustomModel<SoulEntity>(context.bakeLayer(ModelCustomModel.LAYER_LOCATION)), 0f);
		this.addLayer(new RenderLayer<SoulEntity, ModelCustomModel<SoulEntity>>(this) {
			final ResourceLocation LAYER_TEXTURE = ResourceLocation.parse("prapor:textures/entities/soul_texture.png");

			@Override
			public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, SoulEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(LAYER_TEXTURE));
				EntityModel model = new ModelCustomModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelCustomModel.LAYER_LOCATION));
				this.getParentModel().copyPropertiesTo(model);
				model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
				model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
				model.renderToBuffer(poseStack, vertexConsumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0));
			}
		});
	}

	@Override
	protected void scale(SoulEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(2f, 2f, 2f);
	}

	@Override
	public ResourceLocation getTextureLocation(SoulEntity entity) {
		return ResourceLocation.parse("prapor:textures/entities/soul_texture.png");
	}
}
