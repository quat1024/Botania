/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.core.handler;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import vazkii.botania.api.item.IExoflameHeatable;
import vazkii.botania.common.Botania;
import vazkii.botania.mixin.AccessorAbstractFurnaceTileEntity;

public class ExoflameFurnaceHandler {
	public static boolean canSmelt(AbstractFurnaceBlockEntity furnace, Recipe<?> recipe) {
		return ((AccessorAbstractFurnaceTileEntity) furnace).botania_canAcceptRecipeOutput(recipe);
	}

	public static RecipeType<? extends AbstractCookingRecipe> getRecipeType(AbstractFurnaceBlockEntity furnace) {
		return ((AccessorAbstractFurnaceTileEntity) furnace).getRecipeType();
	}

	public static class FurnaceExoflameHeatable implements IExoflameHeatable {
		private final AbstractFurnaceBlockEntity furnace;

		private RecipeType<? extends AbstractCookingRecipe> recipeType;
		private AbstractCookingRecipe currentRecipe;

		public FurnaceExoflameHeatable(AbstractFurnaceBlockEntity furnace) {
			this.furnace = furnace;
		}

		@Override
		public boolean canSmelt() {
			if (furnace.getStack(0).isEmpty()) {
				return false;
			}
			try {
				if (recipeType == null) {
					this.recipeType = ExoflameFurnaceHandler.getRecipeType(furnace);
				}
				if (currentRecipe != null) { // This is already more caching than Mojang does
					if (currentRecipe.matches(furnace, furnace.getWorld())
							&& ExoflameFurnaceHandler.canSmelt(furnace, currentRecipe)) {
						return true;
					}
				}
				currentRecipe = furnace.getWorld().getRecipeManager().getFirstMatch(recipeType, furnace, furnace.getWorld()).orElse(null);
				return ExoflameFurnaceHandler.canSmelt(furnace, currentRecipe);
			} catch (Throwable t) {
				Botania.LOGGER.error("Failed to determine if furnace TE can smelt", t);
				return false;
			}
		}

		@Override
		public int getBurnTime() {
			return ((AccessorAbstractFurnaceTileEntity) furnace).getBurnTime();
		}

		@Override
		public void boostBurnTime() {
			if (getBurnTime() == 0) {
				World world = furnace.getWorld();
				BlockPos pos = furnace.getPos();
				world.setBlockState(pos, world.getBlockState(pos).with(Properties.LIT, true));
			}
			int burnTime = ((AccessorAbstractFurnaceTileEntity) furnace).getBurnTime();
			((AccessorAbstractFurnaceTileEntity) furnace).setBurnTime(burnTime + 200);
		}

		@Override
		public void boostCookTime() {
			int cookTime = ((AccessorAbstractFurnaceTileEntity) furnace).getCookTime();
			((AccessorAbstractFurnaceTileEntity) furnace).setCookTime(Math.min(currentRecipe.getCookTime() - 1, cookTime + 1));
		}
	}
}
