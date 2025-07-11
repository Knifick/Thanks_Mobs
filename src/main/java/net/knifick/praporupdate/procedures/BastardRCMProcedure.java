package net.knifick.praporupdate.procedures;

import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import net.knifick.praporupdate.entity.BastardEntity;
import net.knifick.praporupdate.PraporMod;

import java.util.List;
import java.util.Random;

import static net.knifick.praporupdate.util.bustard.ItemThrower.throwDiamondTowardsPlayer;

public class BastardRCMProcedure {
	public static ItemStack pickupItem = null;

	public static List<ItemStack> loot = List.of(
		new ItemStack(Items.LEATHER_CHESTPLATE),
		new ItemStack(Items.SADDLE),
		new ItemStack(Items.FLETCHING_TABLE),
		new ItemStack(Items.FLETCHING_TABLE),
		new ItemStack(Items.BARREL),
		new ItemStack(Items.CHEST),
		new ItemStack(Items.BELL),
		new ItemStack(Items.ROTTEN_FLESH),
		new ItemStack(Items.FLINT),
		new ItemStack(Items.LAVA_BUCKET),
		new ItemStack(Items.BUCKET),
		new ItemStack(Items.BOOK),
		new ItemStack(Items.BOOKSHELF),
		new ItemStack(Items.WRITABLE_BOOK),
		new ItemStack(Items.IRON_PICKAXE),
		new ItemStack(Items.DIAMOND_HOE),
		new ItemStack(PraporModItems.MUSIC_RECORD_N_42.get())
	);

	public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null || entity.level().isClientSide)
			return;
		Entity player = null;
		Random random = new Random();
		if ((entity instanceof BastardEntity _datEntI ? _datEntI.getEntityData().get(BastardEntity.DATA_State) : 0) == 0) {
			if (entity instanceof BastardEntity) {
				((BastardEntity) entity).setAnimation("find");
			}
			if (entity instanceof BastardEntity _datEntSetI)
				_datEntSetI.getEntityData().set(BastardEntity.DATA_State, 1);
			PraporMod.queueServerWork(91, () -> {
				if (entity instanceof BastardEntity _datEntSetI)
					_datEntSetI.getEntityData().set(BastardEntity.DATA_State, 2);
			});
		} else if ((entity instanceof BastardEntity _datEntI ? _datEntI.getEntityData().get(BastardEntity.DATA_State) : 0) == 2
				&& (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.EMERALD) {
			if (entity instanceof BastardEntity) {
				((BastardEntity) entity).setAnimation("empty");
			}
			if (entity instanceof BastardEntity) {
				((BastardEntity) entity).setAnimation("trade");
			}
			player = sourceentity;
			if (entity instanceof BastardEntity _datEntSetI)
				_datEntSetI.getEntityData().set(BastardEntity.DATA_State, 1);
			if(player instanceof Player plyr) {
				plyr.getMainHandItem().shrink(1);
				int randValue = 0;
				if(pickupItem == null)
					randValue = random.nextInt(loot.size()-1);
				else
					randValue = random.nextInt(loot.size());
				System.out.println("random: "+randValue);
				System.out.println(pickupItem);
				ItemStack randomItem = null;
				if(randValue == loot.size()){
					randomItem = pickupItem;
					pickupItem = null;
					System.out.println("Pickuped item is removed");
				}
				else {
					randomItem = loot.get(randValue);
					System.out.println("usual");
				}
				System.out.println(randomItem.getDisplayName());
				throwDiamondTowardsPlayer(entity.level(), entity, plyr, randomItem);
			}
			if (player instanceof ServerPlayer _player) {
				AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("prapor:bustard_ach"));
				AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
				if (!_ap.isDone()) {
					for (String criteria : _ap.getRemainingCriteria())
						_player.getAdvancements().award(_adv, criteria);
				}
			}
			PraporMod.queueServerWork(35, () -> {
				if (entity instanceof BastardEntity _datEntSetI)
					_datEntSetI.getEntityData().set(BastardEntity.DATA_State, 0);
			});
		} else if ((entity instanceof BastardEntity _datEntI ? _datEntI.getEntityData().get(BastardEntity.DATA_State) : 0) == 2
				&& !((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.EMERALD)) {
			if (entity instanceof BastardEntity) {
				((BastardEntity) entity).setAnimation("empty");
			}
			if (entity instanceof BastardEntity) {
				((BastardEntity) entity).setAnimation("denny");
			}
		}
	}
	public static void addToTradeList(ItemStack item){
		pickupItem = item;
	}
}
