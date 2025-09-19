
package net.knifick.praporupdate.item;

import net.knifick.praporupdate.client.screens.BookScreen;
import net.knifick.praporupdate.entity.*;
import net.knifick.praporupdate.init.PraporModEntities;
import net.knifick.praporupdate.network.PraporModVariables;
import net.knifick.praporupdate.network.payloads.ToastPayload;
import net.knifick.praporupdate.toast.MobToast;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if(!level.isClientSide){
			PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);
			vars.syncPlayerVariables(player);
			return InteractionResult.CONSUME;
		}
		Minecraft.getInstance().setScreen(new BookScreen(Component.literal("Гайд")));
		return super.use(level, player, hand);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand hand) {
		if(player.level().isClientSide) return InteractionResult.SUCCESS;
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
		return InteractionResult.SUCCESS_SERVER;
	}

	public static void addToBook(Player player, LivingEntity entity, int value) {
		PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);
		ResourceLocation id = entity.getType().builtInRegistryHolder().key().location();
		String mobName = id.getPath();

		// текущее знание о мобе
		int currentValue = vars.seenMobs.getOrDefault(mobName, 0);

		// если уже есть этот уровень или выше → ничего не делаем
		if (currentValue >= value) return;

		// проверка последовательности:
		// если хотим записать 2, а нет 1 → запрет
		// если хотим записать 3, а нет 2 → запрет
		if (value > currentValue + 1) return;

		// запись нового уровня
		vars.seenMobs.put(mobName, value);
		vars.syncPlayerVariables(player);

		if (player instanceof ServerPlayer serverPlayer) {
			PacketDistributor.sendToPlayer(serverPlayer, new ToastPayload(mobName));
		}
	}

	public static void showToast(String name) {
		var player = Minecraft.getInstance().player;
		if (player == null) return;

		PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);
		Minecraft mc = Minecraft.getInstance();

		player.playSound(SoundEvents.VILLAGER_WORK_CARTOGRAPHER);

		String info = "Новый моб: ";
		if (vars.seenMobs.getOrDefault(name, 0) > 1) {
			info = "Обновлено: ";
		}

		mc.getToastManager().addToast(
				new MobToast(
						Component.literal(info).append(Component.translatable("entity.prapor." + name)),
						ResourceLocation.fromNamespaceAndPath("prapor", "textures/entity/prapor.png")
				)
		);
	}
}
