
package net.knifick.praporupdate.client.renderer;

import net.knifick.praporupdate.init.PraporModTickets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;

import net.knifick.praporupdate.entity.model.NarratorModel;
import net.knifick.praporupdate.entity.NarratorEntity;

import software.bernie.geckolib.renderer.base.GeoRenderState;

public class NarratorRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<NarratorEntity, R> {
	private static final String NARRATOR_OLD_LOCATION = "narator_reanimated";
	public NarratorRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new NarratorModel());
		this.shadowRadius = 0.8f;
	}

	@Override
	public @Nullable RenderType getRenderType(R renderState, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

	@Override
	public void extractRenderState(NarratorEntity entity, @NotNull R entityRenderState, float partialTick) {
		String s = ChatFormatting.stripFormatting(entity.getName().getString());
		if ("Boombox".equals(s)) {
			entityRenderState.addGeckolibData(PraporModTickets.ENTITY_TEXTURE, NARRATOR_OLD_LOCATION);
		}
		else {
			entityRenderState.addGeckolibData(PraporModTickets.ENTITY_TEXTURE, entity.getTexture());
		}
		entityRenderState.addGeckolibData(PraporModTickets.ENTITY_TAME, entity.isTame());
	}
}
