package net.knifick.praporupdate.procedures;

import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.Entity;

import net.knifick.praporupdate.network.PraporModVariables;

import javax.annotation.Nullable;

@EventBusSubscriber
public class WitherDeathTriggerProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity());
		}
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof WitherBoss) {
			PraporModVariables.MapVariables.get(world).isWitherDead = true;
			PraporModVariables.MapVariables.get(world).syncData(world);
		}
	}
}
