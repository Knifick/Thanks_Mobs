
package net.knifick.praporupdate.client.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.init.PraporModKeyMappings;
import net.knifick.praporupdate.procedures.IsScremerRProcedure;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber({Dist.CLIENT})
public class MantleActivateOverlay {
	private static int ticksSinceEquip = -1; // -1 = не надета/таймер не идёт

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		Player player = Minecraft.getInstance().player;
		if (player == null) return;

		// Проверяем, надета ли мантия
		boolean hasMantle = player.getInventory().getItem(38).is(PraporModItems.MANTLE);

		if (hasMantle) {
			if (ticksSinceEquip < 0) {
				// Только что надели → запускаем таймер
				ticksSinceEquip = 0;
			} else {
				ticksSinceEquip++;
			}
		} else {
			// Сброс, если сняли
			ticksSinceEquip = -1;
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		if (ticksSinceEquip < 0 || ticksSinceEquip > 40) return; // показываем только первые 40 тиков

		int w = event.getGuiGraphics().guiWidth();
		int h = event.getGuiGraphics().guiHeight();
		Font font = Minecraft.getInstance().font;
		Player entity = Minecraft.getInstance().player;
		if (entity == null) return;

		Component text = Component.translatable("screen.prapor.mantle1");
		Component textAfter = Component.translatable("screen.prapor.mantle2");
		KeyMapping key = PraporModKeyMappings.MANTLE_KEYBIND;
		String keyName = key.getTranslatedKeyMessage().getString();

		String fullText = text.getString() + keyName + textAfter.getString();
		int fullWidth = font.width(fullText);
		int x = (w - fullWidth) / 2;
		int y = (int)(h * 0.8);

		// Вычисляем альфу
		float alpha = 1.0f;
		if (ticksSinceEquip > 20) {
			alpha = 1.0f - ((ticksSinceEquip - 20) / 20.0f); // линейно уходит в 0
		}

		// Рисуем с прозрачностью
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
		event.getGuiGraphics().drawString(font, fullText, x, y, 0xFFFFFF, true);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f); // сброс
	}
}

