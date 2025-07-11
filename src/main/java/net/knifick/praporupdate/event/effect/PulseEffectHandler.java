package net.knifick.praporupdate.event.effect;

import net.knifick.praporupdate.init.PraporModMobEffects;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@EventBusSubscriber(modid = "prapor", bus = EventBusSubscriber.Bus.GAME)
public class PulseEffectHandler {
	private static final float SPEED = 1.0f; // Скорость пульсации (циклы в секунду)
	private static final float FADE_SPEED = 1f; // Скорость затухания
	private static long startTime = System.nanoTime();
	private static float pulseAlpha = 0.0f;
	private static boolean fadingOut = false; // Флаг затухания эффекта

	@SubscribeEvent
	public static void onRenderOverlay(RenderGuiLayerEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;
		boolean hasFearEffect = mc.player.hasEffect(PraporModMobEffects.FEAR);
		if (hasFearEffect) {
			// Активен эффект страха - запускаем пульсацию
			long currentTime = System.nanoTime();
			float elapsedSeconds = (currentTime - startTime) / 1_000_000_000.0f;
			pulseAlpha = (float) (0.5f + 0.5f * Math.sin(elapsedSeconds * Math.PI * SPEED));
			fadingOut = false; // Сбрасываем флаг затухания
		} else {
			// Если эффекта больше нет - начинаем плавное затухание
			if (!fadingOut) {
				fadingOut = true;
				startTime = System.nanoTime(); // Перезапускаем таймер
			}
			pulseAlpha = Math.max(0.0f, pulseAlpha - FADE_SPEED);
		}
		// Отрисовка виньетки с текущей прозрачностью
		if (pulseAlpha > 0) {
			event.getGuiGraphics().fillGradient(0, 0, mc.getWindow().getGuiScaledWidth(), (int) (mc.getWindow().getGuiScaledHeight()/1.5),
					(int) (pulseAlpha * 200) << 24, 0);
		}
	}
}