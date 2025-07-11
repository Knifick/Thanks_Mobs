package net.knifick.praporupdate.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

public class AchreddyeProcProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 4, 0.2, 0.2, 0.2, 1);
	}
}
