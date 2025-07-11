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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class IllagerHat {
	@SubscribeEvent
	public static void illagerPerTick(EntityTickEvent.Post event){
		if(!(event.getEntity() instanceof AbstractIllager entity)) return;
		LivingEntity target = entity.getTarget();
		if (target instanceof ServerPlayer player && player.getItemBySlot(EquipmentSlot.HEAD).is(PraporModItems.HAT_HELMET.get()) && target.position().distanceToSqr(entity.position()) > 4) {
			entity.setTarget(null);
		}
	}
}
