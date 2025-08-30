
package net.knifick.praporupdate.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.knifick.praporupdate.entity.MunchsawEntity;
import net.knifick.praporupdate.entity.PraporEntity;
import net.knifick.praporupdate.entity.model.MunchsawModel;
import net.knifick.praporupdate.entity.model.PraporModel;
import net.knifick.praporupdate.procedures.PraporEntityVisualScaleProcedure;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MunchsawRenderer extends GeoEntityRenderer<MunchsawEntity> {
	public MunchsawRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new MunchsawModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	public RenderType getRenderType(MunchsawEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, MunchsawEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

}
