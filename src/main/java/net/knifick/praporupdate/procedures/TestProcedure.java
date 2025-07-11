package net.knifick.praporupdate.procedures;

import net.minecraft.world.entity.Entity;

import net.knifick.praporupdate.entity.NarratorEntity;

public class TestProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof NarratorEntity) {
			((NarratorEntity) entity).setAnimation("empty");
		}
	}
}
