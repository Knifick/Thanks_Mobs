package net.knifick.praporupdate.client.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.knifick.praporupdate.entity.NymphEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class OutlineGeoLayer<T extends GeoAnimatable>
        extends GeoRenderLayer<NymphEntity> {

    private final GeoEntityRenderer<NymphEntity> renderer;

    public OutlineGeoLayer(GeoEntityRenderer<NymphEntity> renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack poseStack, NymphEntity entity, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        var model = this.renderer.getGeoModel().getBakedModel(
                this.renderer.getGeoModel().getModelResource(entity));
        var texture = this.renderer.getTextureLocation(entity);

        RenderType renderTyp = RenderType.entityTranslucent(texture);
        VertexConsumer vc = bufferSource.getBuffer(renderTyp);

        // Задаём максимальное освещение (fullbright)
        int fullBright = 0xF000F0;

        this.renderer.reRender(
                model,
                poseStack,
                bufferSource,
                entity,
                renderType,
                vc,
                partialTick,
                fullBright,
                OverlayTexture.NO_OVERLAY,
                0xFFFFFFFF // белый множитель (оставляет текстуру как есть)
        );
    }
}
