package net.knifick.praporupdate.procedures;

import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

import net.knifick.praporupdate.entity.PraporEntity;

import javax.annotation.Nullable;

@EventBusSubscriber
public class PraporPerTickFilterProcedure {
	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Pre event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof PraporEntity) {
			if (entity.getPersistentData().getBoolean("dance")) {
				if (entity instanceof Mob _entity)
					_entity.getNavigation().stop();
			}
		}
	}
}
