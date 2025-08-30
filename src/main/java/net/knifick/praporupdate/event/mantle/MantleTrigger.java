package net.knifick.praporupdate.event.mantle;

import net.knifick.praporupdate.entity.KillCloud;
import net.knifick.praporupdate.init.PraporModEntities;
import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.init.PraporModParticleTypes;
import net.knifick.praporupdate.init.PraporModSounds;
import net.knifick.praporupdate.network.PraporModVariables;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Random;

@EventBusSubscriber
public class MantleTrigger {
    private static final int MAX_TIME = 10;
    private static final int COOLDOWN = 60;
    private static Vec3 direction = Vec3.ZERO;
    private static int variant = 0;

    @SubscribeEvent
    public static void PlayerPerTick(EntityTickEvent.Post event){
        if(!(event.getEntity() instanceof Player player)) return;
        PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);
        if(vars.mantleTimer > 0){
            vars.mantleTimer++;
            vars.syncPlayerVariables(player);
            if(vars.mantleTimer<MAX_TIME-1)
                player.setDeltaMovement(direction.scale((double) (MAX_TIME - vars.mantleTimer)/3));
            player.hurtMarked = true;
            player.setNoGravity(true);
            KillCloud killCloud = new KillCloud(PraporModEntities.KILL_CLOUD.get(), player.level());
            killCloud.setPos(player.position());
            player.level().addFreshEntity(killCloud);
            killCloud.setPlayerUUID(player.getUUID());
            killCloud.setVariant(variant);
            if(player.level() instanceof ServerLevel level){
                level.sendParticles(PraporModParticleTypes.EYES.get(),
                        player.getX(), player.getY()+1, player.getZ(), 5,
                        1,1,1, 0);
            }
        }
        if(vars.mantleTimer >= MAX_TIME){
            player.setNoGravity(false);
            vars.mantleTimer = 0;
            vars.syncPlayerVariables(player);
        }
    }

    public static void onItemUse(Player player){
        ItemStack itemStack = player.getInventory().getItem(38);
        if (itemStack.is(PraporModItems.MANTLE)) {
            PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);
            if(vars.mantleTimer == 0 && !player.getCooldowns().isOnCooldown(PraporModItems.MANTLE.get())) {
                direction = player.getViewVector(0);
                variant = new Random().nextInt(0,3);
                vars.mantleTimer = 1;
                vars.syncPlayerVariables(player);
                player.getCooldowns().addCooldown(PraporModItems.MANTLE.get(), COOLDOWN+MAX_TIME);
                if(!player.isCreative())
                    itemStack.setDamageValue(itemStack.getDamageValue()+1);
                System.out.println(itemStack.getDamageValue());
                if(itemStack.getDamageValue()==10) {
                    player.playSound(SoundEvents.ITEM_BREAK);
                    player.getInventory().setItem(38, ItemStack.EMPTY);
                }
                if(player.level() instanceof ServerLevel level){
                    level.playSound(null,
                            player.blockPosition(),
                            PraporModSounds.MANTLE_USE.get(),
                            SoundSource.AMBIENT, 1.0f, 1.0f
                    );
                    level.playSound(null,
                            player.blockPosition(),
                            SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM,
                            SoundSource.AMBIENT, 5.0f, 1.0f
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);
            if(vars.mantleTimer>0){
                player.playSound(SoundEvents.ANVIL_LAND, 5, 1);
                event.setCanceled(true);
            }
        }
    }

}
