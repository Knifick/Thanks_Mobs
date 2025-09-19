package net.knifick.praporupdate.client.renderer;

import net.knifick.praporupdate.entity.BobEntity;
import net.knifick.praporupdate.init.PraporModTickets;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;

import net.knifick.praporupdate.entity.model.DarkironkinModel;
import net.knifick.praporupdate.entity.DarkironkinEntity;

import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DarkironkinRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<DarkironkinEntity, R> {
	public DarkironkinRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new DarkironkinModel());
		this.shadowRadius = 1.3f;
	}

	@Override
	public RenderType getRenderType(R renderState, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

	@Override
	public void extractRenderState(DarkironkinEntity entity, R entityRenderState, float partialTick) {
		entityRenderState.addGeckolibData(PraporModTickets.ENTITY_TEXTURE, entity.getTexture());
		entityRenderState.addGeckolibData(PraporModTickets.ENTITY_NAME, entity.getDisplayName().getString());
	}

	@Override
	protected float getDeathMaxRotation(GeoRenderState renderState) {
		return 0;
	}
}
