package net.knifick.praporupdate.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class PraporEntityVisualScaleProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		if (entity instanceof LivingEntity _livEnt0 && _livEnt0.isBaby()) {
			return 0.5;
		}
		return 1;
	}
}
