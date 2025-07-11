package net.knifick.praporupdate.procedures;

import net.minecraft.world.entity.Entity;

public class DanceRetirnerProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return !entity.getPersistentData().getBoolean("dance");
	}
}
