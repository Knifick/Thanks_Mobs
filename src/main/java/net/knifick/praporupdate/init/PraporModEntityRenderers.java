
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.knifick.praporupdate.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.knifick.praporupdate.client.renderer.SoulRenderer;
import net.knifick.praporupdate.client.renderer.PraporRenderer;
import net.knifick.praporupdate.client.renderer.PookerRenderer;
import net.knifick.praporupdate.client.renderer.NarratorRenderer;
import net.knifick.praporupdate.client.renderer.DarkironkinRenderer;
import net.knifick.praporupdate.client.renderer.BrolemRenderer;
import net.knifick.praporupdate.client.renderer.BastardRenderer;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
	}
}
