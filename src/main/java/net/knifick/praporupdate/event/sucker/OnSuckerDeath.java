package net.knifick.praporupdate.event.sucker;

import net.knifick.praporupdate.PraporMod;
import net.knifick.praporupdate.entity.SuckerEntity;
import net.knifick.praporupdate.network.PraporModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = PraporMod.MODID)
public class OnSuckerDeath {
    @SubscribeEvent
    public static void onEntDeath(LivingDeathEvent event){
        if(!(event.getEntity() instanceof SuckerEntity entity)) return;
        if(entity.level().isClientSide) return;
        ServerPlayer owner = (ServerPlayer) entity.getOwner();
        if(owner!=null){
            PraporModVariables.PlayerVariables vars = owner.getData(PraporModVariables.PLAYER_VARIABLES);
            vars.hasSucker = false;
            vars.syncPlayerVariables(owner);
            System.out.println(vars.hasSucker);
        }
    }
}
