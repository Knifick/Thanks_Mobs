package net.knifick.praporupdate.client.renderer;

import net.knifick.praporupdate.entity.MunchsawEntity;
import net.knifick.praporupdate.entity.model.MunchsawModel;
import net.knifick.praporupdate.init.PraporModTickets;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MunchsawRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<MunchsawEntity, R> {
	public MunchsawRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new MunchsawModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	public @Nullable RenderType getRenderType(R renderState, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

	@Override
	public void extractRenderState(MunchsawEntity entity, R entityRenderState, float partialTick) {
		entityRenderState.addGeckolibData(PraporModTickets.ENTITY_TEXTURE, entity.getTexture());
	}
}
