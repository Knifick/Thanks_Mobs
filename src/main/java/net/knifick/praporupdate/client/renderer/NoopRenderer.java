
package net.knifick.praporupdate.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.knifick.praporupdate.entity.BastardEntity;
import net.knifick.praporupdate.entity.model.BastardModel;
import net.knifick.praporupdate.init.PraporModEntities;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NoopRenderer<T extends Entity> extends EntityRenderer<T> {
	public NoopRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return TextureAtlas.LOCATION_BLOCKS; // всё равно не используется
	}

	@Override
	public void render(T entity, float yaw, float partialTicks, PoseStack poseStack,
					   MultiBufferSource buffer, int light) {
		// ничего не рисуем
	}
}