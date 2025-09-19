package net.knifick.praporupdate.init;

import net.knifick.praporupdate.client.renderer.*;
import net.knifick.praporupdate.entity.SuckerEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(value = Dist.CLIENT)
public class PraporModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(PraporModEntities.PRAPOR.get(), PraporRenderer::new);
		event.registerEntityRenderer(PraporModEntities.POOKER.get(), PookerRenderer::new);
		event.registerEntityRenderer(PraporModEntities.SOUL.get(), SoulRenderer::new);
		event.registerEntityRenderer(PraporModEntities.NARRATOR.get(), NarratorRenderer::new);
		event.registerEntityRenderer(PraporModEntities.BASTARD.get(), BastardRenderer::new);
		event.registerEntityRenderer(PraporModEntities.BROLEM.get(), BrolemRenderer::new);
		event.registerEntityRenderer(PraporModEntities.DARKIRONKIN.get(), DarkironkinRenderer::new);
		event.registerEntityRenderer(PraporModEntities.SUCKER.get(), SuckerRenderer::new);
		event.registerEntityRenderer(PraporModEntities.BOB.get(), BobRenderer::new);
		event.registerEntityRenderer(PraporModEntities.NYMPH.get(), NymphRenderer::new);
		event.registerEntityRenderer(PraporModEntities.KILL_CLOUD.get(), NoopRenderer::new);
		event.registerEntityRenderer(PraporModEntities.MUNCHSAW.get(), MunchsawRenderer::new);
	}
}
