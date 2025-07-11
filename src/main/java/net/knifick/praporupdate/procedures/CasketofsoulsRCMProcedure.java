package net.knifick.praporupdate.procedures;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;

import net.knifick.praporupdate.init.PraporModItems;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

public class CasketofsoulsRCMProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
		if (entity == null)
			return;
		ItemStack heldItem = ItemStack.EMPTY;
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PraporModItems.SOUL_BOTTLE.get()
				&& (blockstate.getBlock().getStateDefinition().getProperty("charges") instanceof IntegerProperty _getip3 ? blockstate.getValue(_getip3) : -1) <= 3) {
			{
				int _value = (int) ((blockstate.getBlock().getStateDefinition().getProperty("charges") instanceof IntegerProperty _getip5 ? blockstate.getValue(_getip5) : -1) + 1);
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = world.getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("charges") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
					world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
			}
			if (!(entity instanceof Player _plr ? _plr.getAbilities().instabuild : false)) {
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = new ItemStack(Items.GLASS_BOTTLE).copy();
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
					if (_entity instanceof Player _player)
						_player.getInventory().setChanged();
				}
			}
			if ((blockstate.getBlock().getStateDefinition().getProperty("charges") instanceof IntegerProperty _getip10 ? blockstate.getValue(_getip10) : -1) == 0) {
				{
					int _value = 2;
					BlockPos _pos = BlockPos.containing(x, y, z);
					BlockState _bs = world.getBlockState(_pos);
					if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
						world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
				}
			} else if ((blockstate.getBlock().getStateDefinition().getProperty("charges") instanceof IntegerProperty _getip13 ? blockstate.getValue(_getip13) : -1) == 1) {
				{
					int _value = 3;
					BlockPos _pos = BlockPos.containing(x, y, z);
					BlockState _bs = world.getBlockState(_pos);
					if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
						world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
				}
			} else if ((blockstate.getBlock().getStateDefinition().getProperty("charges") instanceof IntegerProperty _getip16 ? blockstate.getValue(_getip16) : -1) == 2) {
				{
					int _value = 4;
					BlockPos _pos = BlockPos.containing(x, y, z);
					BlockState _bs = world.getBlockState(_pos);
					if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
						world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
				}
			}
			if ((blockstate.getBlock().getStateDefinition().getProperty("charges") instanceof IntegerProperty _getip19 ? blockstate.getValue(_getip19) : -1) == 3) {
				{
					int _value = 5;
					BlockPos _pos = BlockPos.containing(x, y, z);
					BlockState _bs = world.getBlockState(_pos);
					if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
						world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
				}
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.respawn_anchor.set_spawn")), SoundSource.BLOCKS, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.respawn_anchor.set_spawn")), SoundSource.BLOCKS, 1, 1, false);
					}
				}
			} else {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.respawn_anchor.charge")), SoundSource.BLOCKS, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.respawn_anchor.charge")), SoundSource.BLOCKS, 1, 1, false);
					}
				}
			}
		} else if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.ENCHANTED_BOOK
				&& (blockstate.getBlock().getStateDefinition().getProperty("charges") instanceof IntegerProperty _getip26 ? blockstate.getValue(_getip26) : -1) == 4) {
			heldItem = (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
			ItemEnchantments enchantments = heldItem.get(DataComponents.STORED_ENCHANTMENTS);
			boolean canUpgrade = false;
			for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()){
				byte curLevel = (byte) entry.getIntValue();
				byte maxLevel = (byte) entry.getKey().value().getMaxLevel();
				if (curLevel < maxLevel){
					canUpgrade = true;
					break;
				}
			}
			if(!canUpgrade) return;
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.respawn_anchor.deplete")), SoundSource.BLOCKS, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.respawn_anchor.deplete")), SoundSource.BLOCKS, 1, 1, false);
				}
			}
			{
				int _value = 1;
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = world.getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
					world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
			}
			{
				int _value = 0;
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = world.getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("charges") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
					world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
			}
			if (entity instanceof ServerPlayer player)
				EnchantmentGiverProcedure.execute(player);
		}
	}
}