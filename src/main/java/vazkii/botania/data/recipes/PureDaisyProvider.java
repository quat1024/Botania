/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data.recipes;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.crafting.StateIngredientHelper;

import javax.annotation.Nullable;

import java.util.function.Consumer;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class PureDaisyProvider extends RecipesProvider implements BotaniaRecipeProvider {
	public PureDaisyProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void registerRecipes(Consumer<RecipeJsonProvider> consumer) {

		consumer.accept(new FinishedRecipe(id("livingrock"), StateIngredientHelper.of(Blocks.STONE), ModBlocks.livingrock.getDefaultState()));
		consumer.accept(new FinishedRecipe(id("livingwood"), StateIngredientHelper.of(BlockTags.LOGS), ModBlocks.livingwood.getDefaultState()));

		consumer.accept(new FinishedRecipe(id("cobblestone"), StateIngredientHelper.of(Blocks.NETHERRACK), Blocks.COBBLESTONE.getDefaultState()));
		consumer.accept(new FinishedRecipe(id("sand"), StateIngredientHelper.of(Blocks.SOUL_SAND), Blocks.SAND.getDefaultState()));
		consumer.accept(new FinishedRecipe(id("packed_ice"), StateIngredientHelper.of(Blocks.ICE), Blocks.PACKED_ICE.getDefaultState()));
		consumer.accept(new FinishedRecipe(id("blue_ice"), StateIngredientHelper.of(Blocks.PACKED_ICE), Blocks.BLUE_ICE.getDefaultState()));
		consumer.accept(new FinishedRecipe(id("obsidian"), StateIngredientHelper.of(ModBlocks.blazeBlock), Blocks.OBSIDIAN.getDefaultState()));
		consumer.accept(new FinishedRecipe(id("snow_block"), StateIngredientHelper.of(Blocks.WATER), Blocks.SNOW_BLOCK.getDefaultState()));
	}

	@Override
	public String getName() {
		return "Botania Pure Daisy recipes";
	}

	private static Identifier id(String path) {
		return prefix("pure_daisy/" + path);
	}

	private static class FinishedRecipe implements RecipeJsonProvider {
		public static final int DEFAULT_TIME = 150;

		private final Identifier id;
		private final StateIngredient input;
		private final BlockState outputState;
		private final int time;

		private FinishedRecipe(Identifier id, StateIngredient input, BlockState state) {
			this(id, input, state, DEFAULT_TIME);
		}

		private FinishedRecipe(Identifier id, StateIngredient input, BlockState state, int time) {
			Preconditions.checkArgument(time >= 0, "Time must be nonnegative");
			this.id = id;
			this.input = input;
			this.outputState = state;
			this.time = time;
		}

		@Override
		public void serialize(JsonObject json) {
			json.add("input", input.serialize());
			json.add("output", StateIngredientHelper.serializeBlockState(outputState));
			if (time != DEFAULT_TIME) {
				json.addProperty("time", time);
			}
		}

		@Override
		public Identifier getRecipeId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return ModRecipeTypes.PURE_DAISY_SERIALIZER;
		}

		@Nullable
		@Override
		public JsonObject toAdvancementJson() {
			return null;
		}

		@Nullable
		@Override
		public Identifier getAdvancementId() {
			return null;
		}
	}
}
