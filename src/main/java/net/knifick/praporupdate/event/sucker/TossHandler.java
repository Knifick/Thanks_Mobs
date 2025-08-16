package net.knifick.praporupdate.event.sucker;

import net.knifick.praporupdate.entity.SuckerEntity;
import net.knifick.praporupdate.init.PraporModEntities;
import net.knifick.praporupdate.init.PraporModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Objects;
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
            data.putDouble("OwnerX", player.getX());
            data.putDouble("OwnerY", player.getY());
            data.putDouble("OwnerZ", player.getZ());
        }
    }

    @SubscribeEvent
    public static void onItemTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ItemEntity entity)) return;

        if (entity.getY() < 0) {
            CompoundTag data = entity.getPersistentData();

            if (!data.hasUUID("OwnerUUID")) {
                return; // Тега нет — выходим, чтобы не крашиться
            }

            UUID ownerId = data.getUUID("OwnerUUID");
            Vec3 ownerPos = new Vec3(
                    data.getDouble("OwnerX"),
                    data.getDouble("OwnerY"),
                    data.getDouble("OwnerZ")
            );

            if (ownerId != null) {
                entity.level().getServer().sendSystemMessage(Component.literal(ownerId.toString()));
                ServerPlayer owner = (ServerPlayer) entity.level().getPlayerByUUID(ownerId);
                if (owner != null) {
                    SuckerEntity sucker = PraporModEntities.SUCKER.get().create(entity.level());
                    entity.playSound(PraporModSounds.SUCKER_APPROACH.get(), 49, 1);
                    if (sucker != null) {
                        sucker.moveTo(entity.getX(), entity.getY(), entity.getZ(),
                                owner.getYRot(), owner.getXRot());
                        owner.level().addFreshEntity(sucker);
                        sucker.playerPos = ownerPos;
                        sucker.setPlayerUUID(ownerId);
                    }
                    entity.discard();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onSuckerTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof SuckerEntity entity)) return;

        Level level = entity.level();
        // Проверяем, что это серверная сторона и уровень является ServerLevel
        if (!(level instanceof ServerLevel serverLevel)) return;

        MinecraftServer server = serverLevel.getServer();
        if (server == null) return;

        // Получаем UUID владельца
        UUID playerUUID = entity.getPlayerUUID();
        if (playerUUID == null) return;

        ServerPlayer owner = server.getPlayerList().getPlayer(playerUUID);
        if (owner == null) return;

        // Если сущность уже приручена, ничего не делаем
        if (entity.getOwner() != null) return;

        Vec3 entityPos = entity.position();
        Vec3 ownerPos = entity.playerPos;

        // Вектор направления от сущности к игроку
        Vec3 direction = ownerPos.subtract(entityPos);
        double distance = direction.length();

        if (distance > 0.1 && entity.tickCount < 100) {
            // Нормализуем вектор и задаём скорость движения
            Vec3 movement = direction.normalize().scale(0.6).add(0, 0.3, 0);
            entity.setDeltaMovement(movement);

            // Спавним партиклы END_ROD для всех игроков
            serverLevel.sendParticles(
                    ParticleTypes.END_ROD,
                    entityPos.x,
                    entityPos.y,
                    entityPos.z,
                    1,          // количество частиц
                    0.2, 0.2, 0.2, // разброс по XYZ
                    0.05        // скорость частиц
            );
        } else {
            // Достигли игрока — приручаем
            CompoundTag data = entity.getPersistentData();
            //ЗАВТРА ДОДЕЛАЮ СПАВН!!1!!!!!!11111!!!11!11
            data.putUUID("SuckerUUID", owner.getUUID());
            entity.tame(owner);
            entity.setPlayerUUID(null);
            entity.setDeltaMovement(Vec3.ZERO);
        }
    }

}