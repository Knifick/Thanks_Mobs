package net.knifick.praporupdate.mixins;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "hurt", at = @At("HEAD"))
    private void resetInvulnOnHotFloor(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity)(Object)this;
        ResourceKey<DamageType> mantleSource = self.level().holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("prapor:mantle_hurt"))).getKey();
        if (source.is(mantleSource)) {
            self.invulnerableTime = 0;
        }
    }
}
