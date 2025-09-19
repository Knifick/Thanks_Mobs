package net.knifick.praporupdate.client.renderer;

import net.knifick.praporupdate.init.PraporModTickets;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.knifick.praporupdate.entity.model.PraporModel;
import net.knifick.praporupdate.entity.PraporEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class PraporRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<PraporEntity, R> {
	public PraporRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new PraporModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	@Nullable
	public RenderType getRenderType(R renderState, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

	@Override
	public void extractRenderState(PraporEntity entity, R entityRenderState, float partialTick) {
		entityRenderState.addGeckolibData(PraporModTickets.ENTITY_TEXTURE, entity.getTexture());
	}

	@Override
	public void preRender(R renderState, PoseStack poseStack,
						  BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
						  @Nullable VertexConsumer buffer, boolean isReRender,
						  int packedLight, int packedOverlay, int renderColor) {
		float scale = 1f;
		if(renderState.isBaby) scale = 0.5f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(renderState, poseStack, model, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
	}

	@Override
	protected float getDeathMaxRotation(GeoRenderState renderState) {
		return 0f;
	}
}
