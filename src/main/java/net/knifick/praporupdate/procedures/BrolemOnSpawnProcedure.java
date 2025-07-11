package net.knifick.praporupdate.procedures;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import net.knifick.praporupdate.entity.BrolemEntity;

public class BrolemOnSpawnProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(Attributes.ATTACK_SPEED))
			_livingEntity0.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(5);
		if (entity instanceof BrolemEntity) {
			((BrolemEntity) entity).setAnimation("ruins");
		}
		if (entity instanceof BrolemEntity _datEntSetS)
			_datEntSetS.getEntityData().set(BrolemEntity.DATA_xs, ("" + entity.getX()));
		if (entity instanceof BrolemEntity _datEntSetS)
			_datEntSetS.getEntityData().set(BrolemEntity.DATA_zs, ("" + entity.getZ()));
	}
}
