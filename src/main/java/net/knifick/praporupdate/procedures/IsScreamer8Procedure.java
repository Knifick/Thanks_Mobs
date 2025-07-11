package net.knifick.praporupdate.procedures;

import net.minecraft.world.entity.Entity;

import net.knifick.praporupdate.network.PraporModVariables;

public class IsScreamer8Procedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if (entity.getData(PraporModVariables.PLAYER_VARIABLES).screamAnimValue == 8) {
			return true;
		}
		return false;
	}
}
