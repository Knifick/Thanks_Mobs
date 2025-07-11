package net.knifick.praporupdate.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.knifick.praporupdate.block.model.GoldTrophyBlockModel;
import net.knifick.praporupdate.block.entity.GoldTrophyTileEntity;

public class GoldTrophyTileRenderer extends GeoBlockRenderer<GoldTrophyTileEntity> {
	public GoldTrophyTileRenderer() {
		super(new GoldTrophyBlockModel());
	}

	@Override
	public RenderType getRenderType(GoldTrophyTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
