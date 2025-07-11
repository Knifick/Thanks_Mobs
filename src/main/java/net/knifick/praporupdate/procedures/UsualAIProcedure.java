package net.knifick.praporupdate.procedures;

import net.minecraft.world.entity.Entity;

import net.knifick.praporupdate.entity.BastardEntity;

public class UsualAIProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if ((entity instanceof BastardEntity _datEntI ? _datEntI.getEntityData().get(BastardEntity.DATA_State) : 0) > 0) {
			return false;
		}
		return true;
	}
}
