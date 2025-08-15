package net.knifick.praporupdate.event.sucker;

import net.knifick.praporupdate.entity.SuckerEntity;
import net.knifick.praporupdate.init.PraporModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.UUID;

@EventBusSubscriber
public class TossHandler {
    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        Player player = event.getPlayer();
        Level level = player.level();

        if (!level.isClientSide && level.dimension() == Level.END) {
            ItemEntity itemEntity = event.getEntity();
            CompoundTag data = itemEntity.getPersistentData();
            data.putUUID("OwnerUUID", player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onItemTick(EntityTickEvent.Post event){
        if(!(event.getEntity() instanceof ItemEntity entity)) return;
        if(entity.getY() < 0){
            UUID ownerId = entity.getPersistentData().getUUID("OwnerUUID");
            if (ownerId != null) {
                ServerPlayer owner = entity.level().getServer().getPlayerList().getPlayer(ownerId);
                if (owner != null) {
                    SuckerEntity sucker = PraporModEntities.SUCKER.get().create(entity.level());
                    if (sucker != null) {
                        sucker.moveTo(owner.getX(), owner.getY(), owner.getZ(),
                                owner.getYRot(), owner.getXRot());
                        owner.level().addFreshEntity(owner);
                    }
                    entity.discard();
                }
            }

        }
    }
}