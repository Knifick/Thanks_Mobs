package net.knifick.praporupdate.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.knifick.praporupdate.entity.BrolemEntity;

/**
 * Выполняется каждый тик для Brolem'а.
 * Управляет его анимацией и позицией, если он ещё не ожил.
 */
public class BrolemPerTickProcedure {

    public static void execute(double y, Entity entity) {
        if (!(entity instanceof BrolemEntity brolem))
            return;

        boolean isAlive = brolem.getEntityData().get(BrolemEntity.DATA_IsAlive);

        // --- 2. Если Brolem ещё не ожил — телепортируем его на сохранённые координаты ---
        if (!isAlive) {
            double xs = parseDoubleSafe(brolem.getEntityData().get(BrolemEntity.DATA_xs));
            double zs = parseDoubleSafe(brolem.getEntityData().get(BrolemEntity.DATA_zs));

            // Телепорт обычной сущности
            entity.teleportTo(xs, y, zs);

            // Телепорт игрока-сервера (если Brolem — ServerPlayer)
            if (entity instanceof ServerPlayer player) {
                player.connection.teleport(xs, y, zs, player.getYRot(), player.getXRot());
            }
        }
    }

    /**
     * Безопасно конвертирует строку в число.
     * Если не получилось — возвращает 0.
     */
    private static double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception ignored) {
            return 0;
        }
    }
}
