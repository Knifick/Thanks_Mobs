package net.knifick.praporupdate.client.screens;

import net.knifick.praporupdate.network.PraporModVariables;
import net.knifick.praporupdate.util.misc.UIHelper;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;

import net.knifick.praporupdate.procedures.IsScremerRProcedure;

@EventBusSubscriber({Dist.CLIENT})
public class ScreamerOverlay {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getGuiGraphics().guiWidth();
		int h = event.getGuiGraphics().guiHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		PraporModVariables.PlayerVariables vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}
		int a = Mth.clamp((int)((float) ((10-vars.screamAnimValue)/10f) * 255f), 0, 255);
		int color = UIHelper.rgbaToColor(255, 255, 255, a);
		if (IsScremerRProcedure.execute(entity)) {
			event.getGuiGraphics().blit(RenderPipelines.GUI_TEXTURED, ResourceLocation.parse("prapor:textures/screens/screamer1.png"), 0, 0, 0, 0, w, h, w, h, color);
		}
	}
}
