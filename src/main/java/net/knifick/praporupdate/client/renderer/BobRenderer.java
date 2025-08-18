
package net.knifick.praporupdate.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.knifick.praporupdate.entity.BobEntity;
import net.knifick.praporupdate.entity.PookerEntity;
import net.knifick.praporupdate.entity.model.BobModel;
import net.knifick.praporupdate.entity.model.PookerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BobRenderer extends GeoEntityRenderer<BobEntity> {
	public BobRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new BobModel());
		this.shadowRadius = 2f;
	}

	@Override
	public RenderType getRenderType(BobEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, BobEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 3f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}
}
