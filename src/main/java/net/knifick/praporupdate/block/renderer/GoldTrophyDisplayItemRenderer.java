package net.knifick.praporupdate.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.knifick.praporupdate.block.model.GoldTrophyDisplayModel;
import net.knifick.praporupdate.block.display.GoldTrophyDisplayItem;

public class GoldTrophyDisplayItemRenderer extends GeoItemRenderer<GoldTrophyDisplayItem> {
	public GoldTrophyDisplayItemRenderer() {
		super(new GoldTrophyDisplayModel());
	}

	@Override
	public RenderType getRenderType(GoldTrophyDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
