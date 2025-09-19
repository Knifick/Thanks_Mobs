package net.knifick.praporupdate.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.knifick.praporupdate.entity.BastardEntity;
import net.knifick.praporupdate.entity.model.BastardModel;
import net.knifick.praporupdate.init.PraporModTickets;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BastardRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<BastardEntity, R> {
	public BastardRenderer(EntityRendererProvider.Context context) {
		super(context, new BastardModel());
		this.shadowRadius = 0.6f;
	}

	@Override
	public @Nullable RenderType getRenderType(R renderState, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

	@Override
	public void extractRenderState(BastardEntity entity, R entityRenderState, float partialTick) {
		entityRenderState.addGeckolibData(PraporModTickets.ENTITY_TEXTURE, entity.getTexture());
	}

}
