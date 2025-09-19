
package net.knifick.praporupdate.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.knifick.praporupdate.client.layer.OutlineGeoLayer;
import net.knifick.praporupdate.entity.BobEntity;
import net.knifick.praporupdate.entity.NymphEntity;
import net.knifick.praporupdate.entity.PookerEntity;
import net.knifick.praporupdate.entity.model.NymphModel;
import net.knifick.praporupdate.entity.model.PookerModel;
import net.knifick.praporupdate.init.PraporModTickets;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class NymphRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<NymphEntity, R> {
	public NymphRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new NymphModel());
		addRenderLayer(new OutlineGeoLayer<>(this));
		this.shadowRadius = 1f;
	}

	@Override
	public @Nullable RenderType getRenderType(R renderState, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

	@Override
	public void extractRenderState(NymphEntity entity, R entityRenderState, float partialTick) {
		entityRenderState.addGeckolibData(PraporModTickets.ENTITY_TEXTURE, entity.getTexture());
	}

	@Override
	public void preRender(R renderState, PoseStack poseStack,
						  BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
						  @Nullable VertexConsumer buffer, boolean isReRender,
						  int packedLight, int packedOverlay, int renderColor) {
		float scale = 1f;
		if (renderState.isBaby) scale = 0.5f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(renderState, poseStack, model, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
	}
}
