package net.knifick.praporupdate.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CapeLayer.class)
public class CapeLayerMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(PoseStack poseStack,
                          MultiBufferSource buffer,
                          int packedLight,
                          AbstractClientPlayer player,
                          float limbSwing,
                          float limbSwingAmount,
                          float partialTicks,
                          float ageInTicks,
                          float netHeadYaw,
                          float headPitch,
                          CallbackInfo ci) {

        // Получаем предмет в слоте груди
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!chest.isEmpty() && chest.getItem() == PraporModItems.MANTLE.get()) {
            ci.cancel();
        }
    }
}
