package net.knifick.praporupdate.client.renderer;

import net.knifick.praporupdate.item.MantleItem;
import net.knifick.praporupdate.item.model.MantleModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MantleArmorRenderer<R extends HumanoidRenderState & GeoRenderState> extends GeoArmorRenderer<MantleItem, R>{
	public MantleArmorRenderer() {
		super(new MantleModel());
	}

	@Override
	public @Nullable RenderType getRenderType(R renderState, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

	@Override
	protected void applyBoneVisibilityBySlot(EquipmentSlot slot) {
		super.applyBoneVisibilityBySlot(slot);

		if (slot == EquipmentSlot.CHEST) {
            this.head.visible = true;
            this.rightArm.visible = true;
            this.leftArm.visible = true;
        }
	}
}
