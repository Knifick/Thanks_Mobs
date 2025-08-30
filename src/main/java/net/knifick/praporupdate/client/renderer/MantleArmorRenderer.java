package net.knifick.praporupdate.client.renderer;

import net.knifick.praporupdate.item.MantleItem;
import net.knifick.praporupdate.item.model.MantleModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

public class MantleArmorRenderer extends GeoArmorRenderer<MantleItem> {
	public MantleArmorRenderer() {
		super(new MantleModel());
		this.head = new GeoBone(null, "armorHead", false, (double) 0, false, false);
		this.body = new GeoBone(null, "armorBody", false, (double) 0, false, false);
		this.rightArm = new GeoBone(null, "armorRightArm", false, (double) 0, false, false);
		this.leftArm = new GeoBone(null, "armorLeftArm", false, (double) 0, false, false);
		this.rightLeg = new GeoBone(null, "armorRightLeg", false, (double) 0, false, false);
		this.leftLeg = new GeoBone(null, "armorLeftLeg", false, (double) 0, false, false);
		this.rightBoot = new GeoBone(null, "armorRightBoot", false, (double) 0, false, false);
		this.leftBoot = new GeoBone(null, "armorLeftBoot", false, (double) 0, false, false);
	}

	@Override
	public RenderType getRenderType(MantleItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	protected void applyBoneVisibilityBySlot(EquipmentSlot slot) {
		super.applyBoneVisibilityBySlot(slot);

		if (slot == EquipmentSlot.CHEST) {
			if (this.head != null) {
				this.head.setHidden(false);
			}
			if (this.rightArm != null) {
				this.rightArm.setHidden(false);
			}
			if (this.leftArm != null) {
				this.leftArm.setHidden(false);
			}
		}
	}
}
