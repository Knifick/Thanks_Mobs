package net.knifick.praporupdate.init;

import net.knifick.praporupdate.PraporMod;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = PraporMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class PraporModCommands {
    public static GameRules.Key<GameRules.BooleanValue> SPAWN_WITHOUT_WITHER;

    @SubscribeEvent
    public static void registerGameRules(FMLCommonSetupEvent event) {
        SPAWN_WITHOUT_WITHER = GameRules.register("spawnWithoutWither", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(false));
    }

}
