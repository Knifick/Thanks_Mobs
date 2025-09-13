package net.knifick.praporupdate.procedures;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class CasketOfSoulsPerTickProcedure {

	public static void execute(LevelAccessor world, double x, double y, double z) {
		BlockPos pos = BlockPos.containing(x, y, z);

		int charges = getBlockIntData(world, pos, "charges");

		// Если зарядов 3 — создаём частицы
		if (charges == 3 && world instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(
					ParticleTypes.SOUL,
					x, y + 1, z,
					1,
					0.5, 0, 0.5,
					0.01
			);
		}

		// Отправляем сообщение всем игрокам
		if (!world.isClientSide() && world.getServer() != null) {
			world.getServer().getPlayerList().broadcastSystemMessage(
					Component.literal(String.valueOf(charges)),
					false
			);
		}
	}

	/**
	 * Получает сохранённое значение из блока по тегу.
	 * Если блока нет или значения нет, возвращает -1.
	 */
	private static int getBlockIntData(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null) {
			return blockEntity.getPersistentData().getDouble(tag).get().intValue();
		}
		return -1;
	}
}
