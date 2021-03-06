/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.mana;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import vazkii.botania.api.mana.IPoolOverlayProvider;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.common.block.BlockMod;
import vazkii.botania.common.block.tile.mana.TileManaVoid;

import javax.annotation.Nonnull;

public class BlockManaVoid extends BlockMod implements IPoolOverlayProvider, BlockEntityProvider {

	public BlockManaVoid(Settings builder) {
		super(builder);
	}

	@Nonnull
	@Override
	public BlockEntity createBlockEntity(@Nonnull BlockView world) {
		return new TileManaVoid();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Sprite getIcon(World world, BlockPos pos) {
		return MiscellaneousIcons.INSTANCE.manaVoidOverlay.getSprite();
	}

}
