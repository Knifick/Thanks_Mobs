package net.knifick.praporupdate.client.screens;

import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.init.PraporModKeyMappings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = "prapor")
public class MantleActivateOverlay {
	private static int ticksSinceEquip = -1; // -1 = не надета/таймер не идёт

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) return;

		// Слот 38 — нагрудник
		boolean hasMantle = true;
		//boolean hasMantle = player.getInventory().getItem(38).is(PraporModItems.MANTLE);

		if (hasMantle) {
			if (ticksSinceEquip < 0) {
				ticksSinceEquip = 0;
			} else {
				ticksSinceEquip++;
			}
		} else {
			ticksSinceEquip = -1;
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void onRenderGui(RenderGuiEvent.Pre event) {
		if (ticksSinceEquip < 0 || ticksSinceEquip > 40) return;

		var gg = event.getGuiGraphics();
		int w = gg.guiWidth();
		int h = gg.guiHeight();

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) return;

		Font font = mc.font;

		Component text = Component.translatable("screen.prapor.mantle1");
		Component textAfter = Component.translatable("screen.prapor.mantle2");
		KeyMapping key = PraporModKeyMappings.MANTLE_KEYBIND;
		String keyName = key.getTranslatedKeyMessage().getString();

		String fullText = text.getString() + keyName + textAfter.getString();
		int fullWidth = font.width(fullText);
		int x = (w - fullWidth) / 2;
		int y = (int) (h * 0.8f);

		// Альфа: 1.0 на первых 20 тиках, потом линейный фейд до 0 к 40-му тику
		float alphaF = ticksSinceEquip <= 20
				? 1.0f
				: 1.0f - ((ticksSinceEquip - 20) / 20.0f);

		int alpha = Math.max(0, Math.min(255, (int) (alphaF * 255)));
		int color = (alpha << 24) | 0x00FFFFFF; // ARGB

		// Рисуем строку — всё через GuiGraphics (RenderPipeline), без RenderSystem
		gg.drawString(font, fullText, x, y, color, true);
	}
}
