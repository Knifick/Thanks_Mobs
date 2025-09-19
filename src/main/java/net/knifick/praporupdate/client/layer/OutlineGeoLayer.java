package net.knifick.praporupdate.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.knifick.praporupdate.entity.NymphEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class OutlineGeoLayer<R extends LivingEntityRenderState & GeoRenderState>
        extends GeoRenderLayer<NymphEntity, Void, R> {

    private final GeoEntityRenderer<NymphEntity, R> renderer;

    public OutlineGeoLayer(GeoEntityRenderer<NymphEntity, R> renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(R renderState,
                       PoseStack poseStack,
                       BakedGeoModel bakedModel,
                       @Nullable RenderType renderType,
                       MultiBufferSource bufferSource,
                       @Nullable VertexConsumer buffer,
                       int packedLight,
                       int packedOverlay,
                       int renderColor) {

        // текстура из рендера по текущему RenderState
        ResourceLocation texture = renderer.getTextureLocation(renderState);

        // второй проход: можно взять outline/eyes/entityTranslucent — см. примечание ниже
        RenderType pass = RenderType.entityTranslucent(texture);
        VertexConsumer vc = bufferSource.getBuffer(pass);

        int light  = LightTexture.FULL_BRIGHT; // самосвечение; иначе используй packedLight
        int color  = 0xFFFFFFFF;               // белый множитель поверх текстуры

        // ВАЖНО: порядок аргументов как у твоего reRender:
        // (renderState, poseStack, model, bufferSource, renderType, buffer, packedLight, packedOverlay, renderColor)
        renderer.reRender(renderState, poseStack, bakedModel, bufferSource, pass, vc, light, packedOverlay, color);
    }
}
