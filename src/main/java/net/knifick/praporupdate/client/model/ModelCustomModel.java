package net.knifick.praporupdate.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;

public class ModelCustomModel extends EntityModel<LivingEntityRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("prapor", "model_custom_model"), "main");

	private final ModelPart bb_main;

	public ModelCustomModel(ModelPart root) {
		// ВАЖНО: передаём root в суперкласс, вместо override root()
		super(root);
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		root.addOrReplaceChild("bb_main",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(mesh, 8, 8);
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		// пример: синхронизация поворотов с данными из RenderState
		float rad = (float)Math.PI / 180F;
		this.bb_main.xRot = state.xRot * rad;
		this.bb_main.yRot = state.yRot * rad;
	}
}
