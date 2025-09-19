package net.knifick.praporupdate.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(CapeLayer.class)
public class CapeLayerMixin {

    // Mojmap сигнатура 1.21.6/1.21.8:
    // void render(PoseStack, MultiBufferSource, int, PlayerRenderState, float, float)
    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void prapor$cancelCapeIfMantle(
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            PlayerRenderState state,
            float tickDelta,
            float unused, // второй float есть у фич-рендеров
            CallbackInfo ci
    ) {
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!chest.isEmpty() && chest.is(PraporModItems.MANTLE.get())) {
            ci.cancel();
        }

        if (!chest.isEmpty() && chest.is(PraporModItems.MANTLE.get())) {
            ci.cancel(); // не рисуем плащ
        }
    }
}
