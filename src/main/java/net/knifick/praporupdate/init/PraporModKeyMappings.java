package net.knifick.praporupdate.init;

import net.knifick.praporupdate.network.payloads.ClickPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class PraporModKeyMappings {
	public static final KeyMapping MANTLE_KEYBIND = new KeyMapping("key.prapor.mantle_keybind", GLFW.GLFW_KEY_R, "key.categories.prapor") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				ClientPacketListener connection = Minecraft.getInstance().getConnection();
				if(connection!=null)
					connection.getConnection().send(new ServerboundCustomPayloadPacket(new ClickPayload()));
			}
			isDownOld = isDown;
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(MANTLE_KEYBIND);
	}

	@EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(ClientTickEvent.Post event) {
			if (Minecraft.getInstance().screen == null) {
				MANTLE_KEYBIND.consumeClick();
			}
		}
	}
}
