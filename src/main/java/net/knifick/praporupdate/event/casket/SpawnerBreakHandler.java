package net.knifick.praporupdate.event.casket;

import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.block.SpawnerBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class SpawnerBreakHandler {
	@SubscribeEvent
	public static void onSpawnerBreak(BlockEvent.BreakEvent event) {
		// Проверяем, что сломанный блок - это спавнер
		if (event.getState().getBlock() instanceof SpawnerBlock) {
			// Проверяем, что игрок держит кирку
			ItemStack heldItem = event.getPlayer().getMainHandItem();
			if (heldItem.getItem() instanceof PickaxeItem) {
				// Создаем предмет shard
				ItemStack shard = new ItemStack(PraporModItems.SPAWNER_SHARD.get(), 1);

				// Спавним предмет в мире
				ItemEntity itemEntity = new ItemEntity(event.getPlayer().level(),
						event.getPos().getX() + 0.5,
						event.getPos().getY() + 0.5,
						event.getPos().getZ() + 0.5,
						shard);
				event.getPlayer().level().addFreshEntity(itemEntity);
			}
		}
	}
}

