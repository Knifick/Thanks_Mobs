package net.knifick.praporupdate.block.listener;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.block.entity.BlockEntityType;

import net.knifick.praporupdate.init.PraporModBlockEntities;
import net.knifick.praporupdate.block.renderer.GoldTrophyTileRenderer;
import net.knifick.praporupdate.block.entity.GoldTrophyTileEntity;
import net.knifick.praporupdate.PraporMod;

@EventBusSubscriber(modid = PraporMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientListener {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer((BlockEntityType<GoldTrophyTileEntity>) PraporModBlockEntities.GOLD_TROPHY.get(), context -> new GoldTrophyTileRenderer());
	}
}
