
package net.knifick.praporupdate.item;

import net.knifick.praporupdate.client.screens.BookScreen;
import net.knifick.praporupdate.entity.*;
import net.knifick.praporupdate.init.PraporModEntities;
import net.knifick.praporupdate.network.PraporModVariables;
import net.knifick.praporupdate.network.payloads.ToastPayload;
import net.knifick.praporupdate.toast.MobToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class GuideBookItem extends Item {
	public GuideBookItem() {
		super(new Properties().stacksTo(1).rarity(Rarity.RARE));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if(!level.isClientSide){
			PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);
			vars.syncPlayerVariables(player);
			return InteractionResultHolder.consume(player.getItemInHand(hand));
		}
		Minecraft.getInstance().setScreen(new BookScreen(Component.literal("Гайд")));
		return super.use(level, player, hand);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand hand) {
		if(player.level().isClientSide) return InteractionResult.sidedSuccess(true);
		if(!(livingEntity instanceof BastardEntity
		|| livingEntity instanceof BobEntity
		|| livingEntity instanceof BrolemEntity
		|| livingEntity instanceof DarkironkinEntity
		|| livingEntity instanceof NarratorEntity
		|| livingEntity instanceof NymphEntity
		|| livingEntity instanceof PraporEntity
		|| livingEntity instanceof SoulEntity
		|| livingEntity instanceof SuckerEntity)) return InteractionResult.FAIL;
		addToBook(player, livingEntity, 1);
		return InteractionResult.sidedSuccess(false);
	}

	public static void addToBook(Player player, LivingEntity entity, int value){
		PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);
		ResourceLocation id = entity.getType().builtInRegistryHolder().key().location();
		String mobName = id.getPath(); // ← вернёт только "prapor"
		if(vars.seenMobs.get(mobName)==value) return;
		vars.seenMobs.put(mobName, value); // или add в Set
		vars.syncPlayerVariables(player);
		//System.out.println(vars.seenMobs);
		if(player instanceof ServerPlayer serverPlayer)
			PacketDistributor.sendToPlayer(serverPlayer, new ToastPayload(mobName));
	}

	public static void showToast(String name){
		PraporModVariables.PlayerVariables vars = Minecraft.getInstance().player.getData(PraporModVariables.PLAYER_VARIABLES);
		Minecraft mc = Minecraft.getInstance();
		mc.player.playSound(SoundEvents.VILLAGER_WORK_CARTOGRAPHER);
		ToastComponent toastGui = mc.getToasts();

		String info = "Новый моб: ";
		if(vars.seenMobs.get(name)>1)
			info = "Обновлено: ";
		toastGui.addToast(new MobToast(Component.literal(info).append(Component.translatable("entity.prapor."+name)), ResourceLocation.parse("prapor:textures/entity/prapor.png")));

	}
}
