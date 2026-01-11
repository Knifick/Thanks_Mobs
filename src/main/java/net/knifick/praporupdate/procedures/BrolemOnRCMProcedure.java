package net.knifick.praporupdate.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementHolder;

import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.entity.BrolemEntity;
import net.knifick.praporupdate.PraporMod;

public class BrolemOnRCMProcedure {

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
        if (entity == null || sourceentity == null)
            return;

        // Проверяем: в руке Soul Bottle, и Brolem ещё не строится и не живой
        boolean hasSoulBottle = (sourceentity instanceof LivingEntity livEnt)
                && livEnt.getMainHandItem().getItem() == PraporModItems.SOUL_BOTTLE.get();

        boolean isBuilding = (entity instanceof BrolemEntity brolem) && brolem.getEntityData().get(BrolemEntity.DATA_OnBuilding);
        boolean isAlive = (entity instanceof BrolemEntity brolem) && brolem.getEntityData().get(BrolemEntity.DATA_IsAlive);

        if (!hasSoulBottle || isBuilding || isAlive)
            return;

        // --- 1. Обновляем предмет в руке игрока ---
        if (sourceentity instanceof LivingEntity living) {
            ItemStack newStack = new ItemStack(PraporModItems.SOUL_BOTTLE.get());
            newStack.setCount(1);
            living.setItemInHand(InteractionHand.MAIN_HAND, newStack);

            if (living instanceof Player player)
                player.getInventory().setChanged();
        }

        // --- 2. Ставим флаг "строится" и проигрываем анимацию ---
        if (entity instanceof BrolemEntity brolem) {
            brolem.getEntityData().set(BrolemEntity.DATA_OnBuilding, true);
            brolem.setAnimation("empty");
            brolem.setAnimation("build");
        }

        // --- 3. Звуковые эффекты ---
        playSound(world, x, y, z, "prapor:soul_sounds");
        playSound(world, x, y, z, "prapor:build_bro");

        // --- 4. Эффект частиц ---
        if (world instanceof ServerLevel serverLevel)
            serverLevel.sendParticles(ParticleTypes.SOUL, x, y, z, 5, 1, 1, 1, 0.05);

        // --- 5. Через 149 тиков (7.45 сек) активируем Brolem'а ---
        PraporMod.queueServerWork(72, () -> {

            // Достижение для игрока
            if (sourceentity instanceof ServerPlayer player) {
                AdvancementHolder adv = player.server.getAdvancements()
                        .get(ResourceLocation.parse("prapor:brolem_ach"));

                if (adv != null) {
                    AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
                    if (!progress.isDone()) {
                        for (String criteria : progress.getRemainingCriteria())
                            player.getAdvancements().award(adv, criteria);
                    }
                }
            }

            // Завершаем "строительство" и оживляем Brolem'а
            if (entity instanceof BrolemEntity brolem) {
                brolem.getEntityData().set(BrolemEntity.DATA_OnBuilding, false);
                brolem.getEntityData().set(BrolemEntity.DATA_IsAlive, true);
            }

            // Приручаем Brolem'а на владельца
            if (entity instanceof TamableAnimal tamable && sourceentity instanceof Player owner)
                tamable.tame(owner);
        });
    }

    /**
     * Утилита для воспроизведения звука на сервере и клиенте.
     */
    private static void playSound(LevelAccessor world, double x, double y, double z, String soundId) {
        if (!(world instanceof Level level))
            return;

        ResourceLocation soundLoc = ResourceLocation.parse(soundId);

        if (!level.isClientSide()) {
            level.playSound(null, BlockPos.containing(x, y, z),
                    BuiltInRegistries.SOUND_EVENT.get(soundLoc),
                    SoundSource.NEUTRAL, 1f, 1f);
        } else {
            level.playLocalSound(x, y, z,
                    BuiltInRegistries.SOUND_EVENT.get(soundLoc),
                    SoundSource.NEUTRAL, 1f, 1f, false);
        }
    }
}
