/*
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.knifick.praporupdate as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package net.knifick.praporupdate.event.hat;

import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.IronGolem;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;


@EventBusSubscriber
public class GolemHat {
	@SubscribeEvent
	public static void golemPerTick(EntityTickEvent.Post event){
		if(!(event.getEntity() instanceof IronGolem entity)) return;
		// Проверяем, что мы на сервере и что это 10-й тик
		if (!entity.level().isClientSide() && entity.tickCount % 10 == 0) {
			// Радиус поиска (можете поменять на любую дистанцию)
			double searchRadius = 8.0D;

			// Получаем ближайшего игрока
			ServerPlayer nearest = (ServerPlayer) entity.level().getNearestPlayer(entity, searchRadius);

			if (nearest != null && nearest.getItemBySlot(EquipmentSlot.HEAD).is(PraporModItems.HAT_HELMET.get()) && !nearest.gameMode.getGameModeForPlayer().isCreative()) {
				// Или выполнить любое другое действие:
				entity.setTarget(nearest);
			}
		}
	}
}
