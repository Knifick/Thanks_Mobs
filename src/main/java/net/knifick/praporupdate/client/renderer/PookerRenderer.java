
package net.knifick.praporupdate.client.renderer;

import net.knifick.praporupdate.entity.BobEntity;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.knifick.praporupdate.entity.model.PookerModel;
import net.knifick.praporupdate.entity.PookerEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class PookerRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<PookerEntity, R> {
	public PookerRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new PookerModel());
		this.shadowRadius = 2f;
	}

	@Override
	@Nullable
	public RenderType getRenderType(R renderState, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}
