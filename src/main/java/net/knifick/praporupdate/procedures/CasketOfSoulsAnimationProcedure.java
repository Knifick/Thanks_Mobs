package net.knifick.praporupdate.procedures;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class CasketOfSoulsAnimationProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate) {
		int i = world.getRandom().nextInt(0,10);
		if (world instanceof ServerLevel _level && i == 4
		&& (blockstate.getBlock().getStateDefinition().getProperty("charges") instanceof IntegerProperty _getip26 ? blockstate.getValue(_getip26) : -1) == 4)
			_level.sendParticles(ParticleTypes.SOUL, x+0.5, (y + 1.1), z+0.5, 1, 0.2, 0, 0.2, 0.05);
	}
}
