package net.knifick.praporupdate.event.ironkin;

import net.knifick.praporupdate.PraporMod;
import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = PraporMod.MODID)
public class UpgradeActivator {
    @SubscribeEvent
    public static void onItemBurned(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() == PraporModItems.UPGRADE_INACTIVE.get())) return;
        Level world = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        // Проверяем, что предмет горит в лаве
        if (block == Blocks.LAVA || block == Blocks.FIRE || block == Blocks.SOUL_FIRE) {
            ItemStack newStack = new ItemStack(
                    PraporModItems.UPGRADE_ACTIVE.get(),
                    itemEntity.getItem().getCount()
            );

            // Создаём новую сущность предмета
            ItemEntity newEntity = new ItemEntity(
                    world,
                    itemEntity.getX(),
                    itemEntity.getY(),
                    itemEntity.getZ(),
                    newStack
            );

            world.playSound(null, entity.blockPosition(),
                    SoundEvents.GENERIC_BURN, SoundSource.PLAYERS, 1.0F, 1.0F);

            if(!world.isClientSide && world instanceof ServerLevel level_){
                level_.sendParticles(ParticleTypes.FLAME,          // любой эффект: CRIT, END_ROD, SONIC_BOOM и т.д.
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        20,                           // count
                        0.5, 0.5, 0.5,                    // delta (скорость = 0)
                        0.05    );
            }

            // Настраиваем движение как у оригинального предмета
            newEntity.setDeltaMovement(0,0.3,0);

            // Добавляем предмет в мир
            world.addFreshEntity(newEntity);

            // Удаляем оригинальный предмет
            itemEntity.discard();
        }
    }
}
