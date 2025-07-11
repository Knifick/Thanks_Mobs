package net.knifick.praporupdate.event.hat;

import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

@EventBusSubscriber
public class VillagerHat {
	@SubscribeEvent
	public static void onVillagerInteract(PlayerInteractEvent.EntityInteract event) {
		Player player = event.getEntity();
		if (!(event.getTarget() instanceof Villager villager)) return;
		if (player.level().isClientSide() || event.getHand() != InteractionHand.MAIN_HAND) return;

		ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);
		if (headItem.is(PraporModItems.HAT_HELMET.get())) {
			event.setCanceled(true); // отменяем открытие торговли

			double x = villager.getX();
			double y = villager.getY();
			double z = villager.getZ();
			// Проигрываем звук отказа
			villager.level().playSound(null, villager.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0f, 1.0f);
			if(villager.level() instanceof ServerLevel level){
				level.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, y+1.4, z, 4, 0.2, 0.2, 0.2, 1);
			}
		}
	}

}
