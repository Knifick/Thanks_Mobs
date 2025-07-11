package net.knifick.praporupdate.util.bustard;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ItemThrower {
	public static void throwDiamondTowardsPlayer(Level world, Entity mob, Player player, ItemStack item) {
		if (!world.isClientSide) {
			// Создаём сущность предмета (алмаз)
			ItemEntity diamond = new ItemEntity(
					world,
					mob.getX(), mob.getY() + 1, mob.getZ(),
					item
			);

			// Вычисляем направление к игроку
			Vec3 direction = player.position().subtract(mob.position()).normalize().scale(0.5);
			diamond.setDeltaMovement(direction); // Устанавливаем скорость полёта

			// Добавляем в мир
			world.addFreshEntity(diamond);
		}
	}
}
