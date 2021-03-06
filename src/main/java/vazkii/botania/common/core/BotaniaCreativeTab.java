/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.core;

import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.LibMisc;

import javax.annotation.Nonnull;

public final class BotaniaCreativeTab extends ItemGroup {

	public static final BotaniaCreativeTab INSTANCE = new BotaniaCreativeTab();

	public BotaniaCreativeTab() {
		super(computeIndex(), LibMisc.MOD_ID);
		setTexture(LibResources.GUI_CREATIVE);
	}

	private static int computeIndex() {
		((ItemGroupExtensions)ItemGroup.BUILDING_BLOCKS).fabric_expandArray();
		return ItemGroup.GROUPS.length - 1;
	}

	@Nonnull
	@Override
	public ItemStack createIcon() {
		return new ItemStack(ModItems.lexicon);
	}
}
