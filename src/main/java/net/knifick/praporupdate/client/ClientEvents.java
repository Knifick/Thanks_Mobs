package net.knifick.praporupdate.client;

import net.knifick.praporupdate.PraporMod;
import net.knifick.praporupdate.util.ironkin.ScreenShakeUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

import java.util.Random;

@EventBusSubscriber(modid = PraporMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEvents {

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		ScreenShakeUtil.decreaseShake();
	}

	@SubscribeEvent
	public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
		if (ScreenShakeUtil.getShakeDuration() > 0) {
			float strength = ScreenShakeUtil.getShakeStrength();
			Random rand = new Random();

			// Добавляем случайные смещения к углам камеры
			event.setYaw(event.getYaw() + (rand.nextFloat() - 0.5F) * strength);
			event.setPitch(event.getPitch() + (rand.nextFloat() - 0.5F) * strength * 0.5F);
		}
	}
}