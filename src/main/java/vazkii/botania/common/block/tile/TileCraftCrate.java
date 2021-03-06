/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.world.World;

import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.CratePattern;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;

import java.util.List;
import java.util.Optional;

public class TileCraftCrate extends TileOpenCrate {
	private static final String TAG_CRAFTING_RESULT = "craft_result";
	private int signal = 0;
	private ItemStack craftResult = ItemStack.EMPTY;

	public TileCraftCrate() {
		super(ModTiles.CRAFT_CRATE);
	}

	@Override
	protected SimpleInventory createItemHandler() {
		return new SimpleInventory(9) {
			@Override
			public int getMaxCountPerStack() {
				return 1;
			}

			@Override
			public boolean isValid(int slot, ItemStack stack) {
				return !isLocked(slot);
			}
		};
	}

	public CratePattern getPattern() {
		BlockState state = getCachedState();
		if (state.getBlock() != ModBlocks.craftCrate) {
			return CratePattern.NONE;
		}
		return state.get(BotaniaStateProps.CRATE_PATTERN);
	}

	private boolean isLocked(int slot) {
		return !getPattern().openSlots.get(slot);
	}

	@Override
	public void readPacketNBT(CompoundTag tag) {
		super.readPacketNBT(tag);
		craftResult = ItemStack.fromTag(tag.getCompound(TAG_CRAFTING_RESULT));
	}

	@Override
	public void writePacketNBT(CompoundTag tag) {
		super.writePacketNBT(tag);
		tag.put(TAG_CRAFTING_RESULT, craftResult.toTag(new CompoundTag()));
	}

	@Override
	public void tick() {
		if (world.isClient) {
			return;
		}

		if (canEject() && isFull() && craft(true)) {
			ejectAll();
		}

		int newSignal = 0;
		for (; newSignal < 9; newSignal++) // dis for loop be derpy
		{
			if (!isLocked(newSignal) && getItemHandler().getStack(newSignal).isEmpty()) {
				break;
			}
		}

		if (newSignal != signal) {
			signal = newSignal;
			world.updateComparators(pos, getCachedState().getBlock());
		}
	}

	private boolean craft(boolean fullCheck) {
		if (fullCheck && !isFull()) {
			return false;
		}

		CraftingInventory craft = new CraftingInventory(new ScreenHandler(ScreenHandlerType.CRAFTING, -1) {
			@Override
			public boolean canUse(@Nonnull PlayerEntity player) {
				return false;
			}
		}, 3, 3);
		for (int i = 0; i < craft.size(); i++) {
			ItemStack stack = getItemHandler().getStack(i);

			if (stack.isEmpty() || isLocked(i) || stack.getItem() == ModItems.placeholder) {
				continue;
			}

			craft.setStack(i, stack);
		}

		Optional<CraftingRecipe> matchingRecipe = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craft, world);
		matchingRecipe.ifPresent(recipe -> {
			craftResult = recipe.craft(craft);

			List<ItemStack> remainders = recipe.getRemainingStacks(craft);
			for (int i = 0; i < craft.size(); i++) {
				ItemStack s = remainders.get(i);
				if (!getItemHandler().getStack(i).isEmpty()
						&& getItemHandler().getStack(i).getItem() == ModItems.placeholder) {
					continue;
				}
				getItemHandler().setStack(i, s);
			}
		});

		return matchingRecipe.isPresent();
	}

	boolean isFull() {
		for (int i = 0; i < getItemHandler().size(); i++) {
			if (!isLocked(i) && getItemHandler().getStack(i).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	private void ejectAll() {
		for (int i = 0; i < inventorySize(); ++i) {
			ItemStack stack = getItemHandler().getStack(i);
			if (!stack.isEmpty()) {
				eject(stack, false);
			}
			getItemHandler().setStack(i, ItemStack.EMPTY);
		}
		if (!craftResult.isEmpty()) {
			eject(craftResult, false);
			craftResult = ItemStack.EMPTY;
		}
	}

	public boolean onWanded(World world) {
		if (!world.isClient && canEject()) {
			craft(false);
			ejectAll();
		}
		return true;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (world != null && !world.isClient) {
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}

	public int getSignal() {
		return signal;
	}

}
