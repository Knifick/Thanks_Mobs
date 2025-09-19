package net.knifick.praporupdate.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.knifick.praporupdate.client.model.ModelCustomModel;
import net.knifick.praporupdate.entity.SoulEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;

/**
 * NeoForge 1.21.8 renderer using RenderState API.
 * NOTE: ModelCustomModel must extend EntityModel<LivingEntityRenderState>.
 */
public final class SoulRenderer
		extends LivingEntityRenderer<SoulEntity, LivingEntityRenderState, ModelCustomModel> {

	private static final ResourceLocation BASE_TEXTURE =
			ResourceLocation.parse("prapor:textures/entities/soul_texture.png");
	private static final ResourceLocation EYES_TEXTURE =
			ResourceLocation.parse("prapor:textures/entities/soul_texture.png"); // замените при желании на отдельную текстуру глаз

	public SoulRenderer(EntityRendererProvider.Context ctx) {
		// базовая модель + радиус тени
		super(ctx, new ModelCustomModel(ctx.bakeLayer(ModelCustomModel.LAYER_LOCATION)), 0.0f);

		// добавляем собственный слой (пример — “глаза” c RenderType.eyes)
		this.addLayer(new EyesLayer(this, ctx.getModelSet()));
	}

	// === RenderState lifecycle ===

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public void extractRenderState(SoulEntity entity, LivingEntityRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		// сюда можно положить свои данные в state.setRenderData(...) при необходимости
	}

	@Override
	public void render(LivingEntityRenderState state, PoseStack poseStack,
					   MultiBufferSource buffer, int packedLight) {
		// рендер базовой модели + всех зарегистрированных RenderLayer’ов
		super.render(state, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(LivingEntityRenderState state) {
		return BASE_TEXTURE;
	}

	// масштабирование теперь принимает state вместо entity (см. NeoForge docs)
	@Override
	protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
		poseStack.scale(2f, 2f, 2f);
	}

	// === Custom RenderLayer example (emissive eyes) ===
	private static final class EyesLayer extends RenderLayer<LivingEntityRenderState, ModelCustomModel> {
		private final ModelCustomModel model;

		EyesLayer(SoulRenderer parent, EntityModelSet models) {
			super(parent);
			// при желании можно использовать отдельную “глазную” модель/лейер
			this.model = new ModelCustomModel(models.bakeLayer(ModelCustomModel.LAYER_LOCATION));
		}

		@Override
		public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
						   LivingEntityRenderState state, float yRot, float xRot) {

			// подготавливаем модель от текущего состояния
			this.model.setupAnim(state);

			// берём буфер под emissive глаза
			VertexConsumer vc = bufferSource.getBuffer(RenderType.eyes(EYES_TEXTURE));

			// Overlay теперь считается от state (без доступа к entity)
			int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0f);

			// Рисуем наш слой поверх базовой модели
			this.model.renderToBuffer(poseStack, vc, packedLight, overlay);
		}
	}
}
