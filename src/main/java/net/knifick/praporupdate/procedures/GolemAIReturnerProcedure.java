package net.knifick.praporupdate.procedures;

import net.minecraft.world.entity.Entity;

import net.knifick.praporupdate.entity.BrolemEntity;

public class GolemAIReturnerProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return entity instanceof BrolemEntity _datEntL0 && _datEntL0.getEntityData().get(BrolemEntity.DATA_IsAlive);
	}
}
