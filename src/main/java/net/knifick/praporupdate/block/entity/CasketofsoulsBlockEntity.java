package net.knifick.praporupdate.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.ContainerHelper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;

import net.knifick.praporupdate.init.PraporModBlockEntities;

import java.util.stream.IntStream;

public class CasketofsoulsBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
	private NonNullList<ItemStack> stacks = NonNullList.withSize(9, ItemStack.EMPTY);
	private final SidedInvWrapper handler = new SidedInvWrapper(this, null);

	public CasketofsoulsBlockEntity(BlockPos pos, BlockState state) {
		super(PraporModBlockEntities.CASKETOFSOULS.get(), pos, state);
	}

	/* ========= Value I/O ========= */

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);

		// Если у блока не привязан лут-тейбл — читаем слоты
		if (!this.tryLoadLootTable(input)) {
			this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
			ContainerHelper.loadAllItems(input, this.stacks);
		}
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);

		// Если не сохраняем ссылку на лут-тейбл — пишем инвентарь
		if (!this.trySaveLootTable(output)) {
			ContainerHelper.saveAllItems(output, this.stacks);
		}
	}

	/* ========= Синхронизация с клиентом (без изменений по 1.21.8) ========= */

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public net.minecraft.nbt.CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
		// можно оставить saveWithFullMetadata/saveWithoutMetadata — как и раньше
		return this.saveWithFullMetadata(lookup);
	}

	/* ========= Container / WorldlyContainer ========= */

	@Override public int getContainerSize() { return stacks.size(); }
	@Override public boolean isEmpty() { return stacks.stream().allMatch(ItemStack::isEmpty); }
	@Override public Component getDefaultName() { return Component.literal("casketofsouls"); }
	@Override public int getMaxStackSize() { return 64; }
	@Override public AbstractContainerMenu createMenu(int id, Inventory inv) { return ChestMenu.threeRows(id, inv); }
	@Override public Component getDisplayName() { return Component.literal("Casket of souls"); }

	@Override protected NonNullList<ItemStack> getItems() { return this.stacks; }
	@Override protected void setItems(NonNullList<ItemStack> stacks) { this.stacks = stacks; }
	@Override public boolean canPlaceItem(int index, ItemStack stack) { return true; }

	@Override
	public int[] getSlotsForFace(Direction side) {
		return IntStream.range(0, this.getContainerSize()).toArray();
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction dir) {
		return this.canPlaceItem(index, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction dir) {
		return true;
	}

	public SidedInvWrapper getItemHandler() { return handler; }
}
