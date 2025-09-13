package net.knifick.praporupdate.procedures;

import net.knifick.praporupdate.init.PraporModCommands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

import net.knifick.praporupdate.network.PraporModVariables;

public class SoulSpawnConditionProcedure {
	public static boolean execute(LevelAccessor world) {
		return PraporModVariables.MapVariables.get(world).isWitherDead
				|| (world instanceof ServerLevel level && level.getServer().getGameRules().getBoolean(PraporModCommands.SPAWN_WITHOUT_WITHER));
	}
}
