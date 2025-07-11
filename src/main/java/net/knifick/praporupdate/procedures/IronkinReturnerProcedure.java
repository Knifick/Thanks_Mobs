package net.knifick.praporupdate.procedures;

import net.minecraft.world.entity.Entity;

import net.knifick.praporupdate.entity.DarkironkinEntity;

public class IronkinReturnerProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return !(entity instanceof DarkironkinEntity _datEntL0 && _datEntL0.getEntityData().get(DarkironkinEntity.DATA_IsGrAttack))
				&& !(entity instanceof DarkironkinEntity _datEntL1 && _datEntL1.getEntityData().get(DarkironkinEntity.DATA_IsSmAttack));
	}
}
