package net.knifick.praporupdate.mixins;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayer.class)
public abstract class PlayerNoClipMixin{
}