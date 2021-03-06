/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.string;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import vazkii.botania.common.block.tile.string.TileRedString;
import vazkii.botania.common.block.tile.string.TileRedStringRelay;

import javax.annotation.Nonnull;

public class BlockRedStringRelay extends BlockRedString {

	public BlockRedStringRelay(AbstractBlock.Settings builder) {
		super(builder);
		setDefaultState(getDefaultState().with(Properties.FACING, Direction.DOWN));
	}

	@Nonnull
	@Override
	public TileRedString createBlockEntity(@Nonnull BlockView world) {
		return new TileRedStringRelay();
	}

}
