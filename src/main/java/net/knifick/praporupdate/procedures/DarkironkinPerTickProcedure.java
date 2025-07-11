package net.knifick.praporupdate.procedures;

import net.minecraft.world.entity.Entity;

import net.knifick.praporupdate.entity.DarkironkinEntity;

public class DarkironkinPerTickProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof DarkironkinEntity _datEntL0 && _datEntL0.getEntityData().get(DarkironkinEntity.DATA_IsGrAttack))
				&& (entity instanceof DarkironkinEntity _datEntI ? _datEntI.getEntityData().get(DarkironkinEntity.DATA_GrTimer) : 0) > 0) {
			if (entity instanceof DarkironkinEntity _datEntSetI)
				_datEntSetI.getEntityData().set(DarkironkinEntity.DATA_GrTimer, (int) ((entity instanceof DarkironkinEntity _datEntI ? _datEntI.getEntityData().get(DarkironkinEntity.DATA_GrTimer) : 0) - 1));
		}
		if (entity instanceof DarkironkinEntity _datEntL4 && _datEntL4.getEntityData().get(DarkironkinEntity.DATA_IsGrAttack)
				&& (entity instanceof DarkironkinEntity _datEntI ? _datEntI.getEntityData().get(DarkironkinEntity.DATA_GrSwitchTimer) : 0) > 0) {
			if (entity instanceof DarkironkinEntity _datEntSetI)
				_datEntSetI.getEntityData().set(DarkironkinEntity.DATA_GrSwitchTimer, (int) ((entity instanceof DarkironkinEntity _datEntI ? _datEntI.getEntityData().get(DarkironkinEntity.DATA_GrSwitchTimer) : 0) - 1));
		} else if (entity instanceof DarkironkinEntity _datEntL8 && _datEntL8.getEntityData().get(DarkironkinEntity.DATA_IsGrAttack)
				&& (entity instanceof DarkironkinEntity _datEntI ? _datEntI.getEntityData().get(DarkironkinEntity.DATA_GrSwitchTimer) : 0) == 0) {
			if (entity instanceof DarkironkinEntity _datEntSetL)
				_datEntSetL.getEntityData().set(DarkironkinEntity.DATA_IsGrAttack, false);
		}
		if (entity instanceof DarkironkinEntity _datEntL11 && _datEntL11.getEntityData().get(DarkironkinEntity.DATA_IsSmAttack)
				&& (entity instanceof DarkironkinEntity _datEntI ? _datEntI.getEntityData().get(DarkironkinEntity.DATA_SmSwitchTimer) : 0) > 0) {
			if (entity instanceof DarkironkinEntity _datEntSetI)
				_datEntSetI.getEntityData().set(DarkironkinEntity.DATA_SmSwitchTimer, (int) ((entity instanceof DarkironkinEntity _datEntI ? _datEntI.getEntityData().get(DarkironkinEntity.DATA_SmSwitchTimer) : 0) - 1));
		} else if (entity instanceof DarkironkinEntity _datEntL15 && _datEntL15.getEntityData().get(DarkironkinEntity.DATA_IsSmAttack)
				&& (entity instanceof DarkironkinEntity _datEntI ? _datEntI.getEntityData().get(DarkironkinEntity.DATA_SmSwitchTimer) : 0) == 0) {
			if (entity instanceof DarkironkinEntity _datEntSetL)
				_datEntSetL.getEntityData().set(DarkironkinEntity.DATA_IsSmAttack, false);
			if (entity instanceof DarkironkinEntity _datEntSetL)
				_datEntSetL.getEntityData().set(DarkironkinEntity.DATA_SmSwitcher, true);
		}
	}
}
