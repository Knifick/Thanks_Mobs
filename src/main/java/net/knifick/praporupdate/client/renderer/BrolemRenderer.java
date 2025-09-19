
package net.knifick.praporupdate.client.renderer;

import net.knifick.praporupdate.init.PraporModTickets;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;

import net.knifick.praporupdate.entity.model.BrolemModel;
import net.knifick.praporupdate.entity.BrolemEntity;

import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BrolemRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<BrolemEntity, R> {
	public BrolemRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new BrolemModel());
		this.shadowRadius = 1f;
	}

	@Override
	public RenderType getRenderType(R renderState, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

	@Override
	public void extractRenderState(BrolemEntity entity, R entityRenderState, float partialTick) {
		entityRenderState.addGeckolibData(PraporModTickets.ENTITY_TEXTURE, entity.getTexture());
	}
}
