package net.knifick.praporupdate.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.knifick.praporupdate.network.PraporModVariables;

public class SoulSpawnConditionProcedure {
	public static boolean execute(LevelAccessor world) {
		return PraporModVariables.MapVariables.get(world).isWitherDead;
	}
}
