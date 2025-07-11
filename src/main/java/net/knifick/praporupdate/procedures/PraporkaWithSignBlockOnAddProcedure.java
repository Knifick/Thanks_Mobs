package net.knifick.praporupdate.procedures;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import java.util.UUID;

public class PraporkaWithSignBlockOnAddProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		String uid = "";
		Entity prapEnt = null;
		uid = new Object() {
			public String getValue(LevelAccessor world, BlockPos pos, String tag) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null)
					return blockEntity.getPersistentData().getString(tag);
				return "";
			}
		}.getValue(world, BlockPos.containing(x, y, z), "tag");
		if (!world.isClientSide()) {
			ServerLevel serverLevel = (ServerLevel) world;
			if (serverLevel.getEntity(UUID.fromString(uid)) != null) {
				prapEnt = serverLevel.getEntity(UUID.fromString(uid));
			}
		}
		if (prapEnt instanceof Mob _entity)
			_entity.getNavigation().moveTo(x, y, z, 1);
	}
}
