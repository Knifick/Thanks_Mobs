package net.knifick.praporupdate.network;

import net.knifick.praporupdate.PraporMod;
import net.knifick.praporupdate.client.effects.RingSuckEffectD;
import net.knifick.praporupdate.event.mantle.MantleTrigger;
import net.knifick.praporupdate.item.GuideBookItem;
import net.knifick.praporupdate.network.payloads.ClickPayload;
import net.knifick.praporupdate.network.payloads.RingSuckPayload;
import net.knifick.praporupdate.network.payloads.ToastPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = PraporMod.MODID)
public class PayloadRegister {
    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1"); // Укажите версию протокола

        registrar.playToClient(
                RingSuckPayload.TYPE,
                RingSuckPayload.STREAM_CODEC,
                (payload, context) -> {
                    // Вызов метода на клиенте
                    context.enqueueWork(()->RingSuckEffectD.trigger(
                            payload.x(), payload.y(), payload.z(),
                            payload.dx(), payload.dy(), payload.dz()));
                }
        );
        registrar.playToClient(
                ToastPayload.TYPE,
                ToastPayload.STREAM_CODEC,
                (payload, context) -> {
                    // Вызов метода на клиенте
                    context.enqueueWork(()-> GuideBookItem.showToast(payload.mob()));
                }
        );
        registrar.playToServer(
                ClickPayload.TYPE,
                ClickPayload.STREAM_CODEC,
                (payload, context) ->{
                    context.enqueueWork(()-> MantleTrigger.onItemUse(context.player()));
                }
        );
    }
}
