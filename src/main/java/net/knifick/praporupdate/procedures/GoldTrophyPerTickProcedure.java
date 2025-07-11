package net.knifick.praporupdate.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

public class GoldTrophyPerTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.GLOW, x, (y + 1), z, 1, 0.5, 1, 0.1, 0.01);
	}
}
