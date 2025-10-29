package net.knifick.praporupdate.event.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class ChestLockHandler {
    private static final String LOCK_KEY = "PraporLock";

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock e) {
        if (e.getLevel().isClientSide()) return;

        BlockPos pos = e.getPos();
        BlockEntity be = e.getLevel().getBlockEntity(pos);
        if (be instanceof ChestBlockEntity chest) {
            if (chest.getPersistentData().getInt(LOCK_KEY) > 0) {
                // сундук “под замком” — запретить открытие
                e.setCanceled(true);
                e.setCancellationResult(InteractionResult.FAIL);
            }
        }
    }
}