package net.knifick.praporupdate.init;

import net.knifick.praporupdate.client.particle.EyesParticle;
import net.knifick.praporupdate.client.particle.KillCloudParticle;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.knifick.praporupdate.client.particle.SparksParticle;

@EventBusSubscriber(value = Dist.CLIENT)
public class PraporModParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(PraporModParticleTypes.SPARKS.get(), SparksParticle::provider);
		event.registerSpriteSet(PraporModParticleTypes.EYES.get(), EyesParticle::provider);
		event.registerSpriteSet(PraporModParticleTypes.KILL_CLOUD.get(), KillCloudParticle::provider);
	}
}
