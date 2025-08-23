
package net.knifick.praporupdate.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.knifick.praporupdate.client.layer.OutlineGeoLayer;
import net.knifick.praporupdate.entity.NymphEntity;
import net.knifick.praporupdate.entity.PookerEntity;
import net.knifick.praporupdate.entity.model.NymphModel;
import net.knifick.praporupdate.entity.model.PookerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NymphRenderer extends GeoEntityRenderer<NymphEntity> {
	public NymphRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new NymphModel());
		addRenderLayer(new OutlineGeoLayer<>(this));
		this.shadowRadius = 1f;
	}

	@Override
	public RenderType getRenderType(NymphEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, NymphEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1f;
		if (entity.isBaby()) scale = 0.5f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}
}
