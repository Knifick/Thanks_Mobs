
package net.knifick.praporupdate.client.renderer;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.animal.Rabbit;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.knifick.praporupdate.entity.model.NarratorModel;
import net.knifick.praporupdate.entity.NarratorEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class NarratorRenderer extends GeoEntityRenderer<NarratorEntity> {
	private static final ResourceLocation NARRATOR_DEFAULT_LOCATION = ResourceLocation.fromNamespaceAndPath("prapor","textures/entities/narator_animated.png");
	private static final ResourceLocation NARRATOR_DICTOR_LOCATION = ResourceLocation.fromNamespaceAndPath("prapor","textures/entities/dictor_animated.png");
	private static final ResourceLocation NARRATOR_OLD_LOCATION = ResourceLocation.fromNamespaceAndPath("prapor","textures/entities/narator_reanimated.png");
	public NarratorRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new NarratorModel());
		this.shadowRadius = 0.8f;
	}

	@Override
	public RenderType getRenderType(NarratorEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public ResourceLocation getTextureLocation(NarratorEntity entity) {
		String s = ChatFormatting.stripFormatting(entity.getName().getString());
		if ("Boombox".equals(s)) {
			return NARRATOR_OLD_LOCATION;
		}
		else{
			if(entity.getVariant() == 1){
				return NARRATOR_DEFAULT_LOCATION;
			}
			else if(entity.getVariant() == 2){
				return NARRATOR_DICTOR_LOCATION;
			}
			else return NARRATOR_DEFAULT_LOCATION;
		}
	}

	@Override
	public void preRender(PoseStack poseStack, NarratorEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}
}
