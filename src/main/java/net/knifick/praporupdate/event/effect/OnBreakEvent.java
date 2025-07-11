package net.knifick.praporupdate.event.effect;

import net.knifick.praporupdate.init.PraporModMobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Random;

@EventBusSubscriber(modid = "prapor", bus = EventBusSubscriber.Bus.GAME)
public class OnBreakEvent {
	private static final Random random = new Random();

	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		if (hasFearEffect(player) && random.nextFloat() < 0.4f) {
			event.setCanceled(true);
		}
	}

	private static boolean hasFearEffect(Player player) {
		return player.hasEffect(PraporModMobEffects.FEAR);
	}
}