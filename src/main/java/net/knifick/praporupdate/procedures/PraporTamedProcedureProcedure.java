package net.knifick.praporupdate.procedures;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

public class PraporTamedProcedureProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if ((entity instanceof TamableAnimal _tamEnt ? _tamEnt.isTame() : false) && ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) instanceof TamableAnimal _tamEnt ? _tamEnt.isTame() : false)) {
			return false;
		}
		return true;
	}
}
