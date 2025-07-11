package net.knifick.praporupdate.procedures;

import net.minecraft.world.entity.Entity;

import net.knifick.praporupdate.network.PraporModVariables;

public class IsScremerRProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if (entity.getData(PraporModVariables.PLAYER_VARIABLES).screamAnimValue == 1) {
			return true;
		}
		return false;
	}
}
