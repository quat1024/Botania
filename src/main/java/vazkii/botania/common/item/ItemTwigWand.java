/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Jan 20, 2014, 7:42:46 PM (GMT)]
 */
package vazkii.botania.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.wand.ICoordBoundItem;
import vazkii.botania.api.wand.ITileBound;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.BlockPistonRelay;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.mana.BlockEnchanter;
import vazkii.botania.common.block.tile.TileEnchanter;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.core.helper.PlayerHelper;
import vazkii.botania.common.core.helper.Vector3;
import vazkii.botania.common.lib.LibItemNames;
import vazkii.botania.common.lib.LibMisc;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.List;

public class ItemTwigWand extends Item16Colors implements ICoordBoundItem {

	private static final String TAG_COLOR1 = "color1";
	private static final String TAG_COLOR2 = "color2";
	private static final String TAG_BOUND_TILE_X = "boundTileX";
	private static final String TAG_BOUND_TILE_Y = "boundTileY";
	private static final String TAG_BOUND_TILE_Z = "boundTileZ";
	private static final BlockPos UNBOUND_POS = new BlockPos(0, -1, 0);

	public ItemTwigWand() {
		super(LibItemNames.TWIG_WAND);
		setMaxStackSize(1);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		Block block = world.getBlockState(pos).getBlock();
		BlockPos boundPos = getBoundTile(stack);
		TileEntity boundTile = world.getTileEntity(boundPos);

		if(player.isSneaking()) {
			//Handle creating the Mana Enchanter
			if(block == Blocks.LAPIS_BLOCK && ConfigHandler.enchanterEnabled) {
				return BlockEnchanter.handleCreation(player, world, pos);
			}
			
			//Handle general IWandable clicks??
			if(block instanceof IWandable) {
				TileEntity tile = world.getTileEntity(pos);
				boolean bindable = tile instanceof IWandBindable;
				
				boolean wanded;
				if(bindable && player.isSneaking() && ((IWandBindable) tile).canSelect(player, stack, pos, side)) {
					if(boundPos.equals(pos))
						setBoundTile(stack, UNBOUND_POS);
					else setBoundTile(stack, pos);
					
					if(world.isRemote) {
						player.swingArm(hand);
						player.playSound(ModSounds.ding, 0.11F, 1F);
					}
					
					wanded = true;
				} else {
					wanded = ((IWandable) block).onUsedByWand(player, stack, world, pos, side);
					if(wanded && world.isRemote)
						player.swingArm(hand);
				}
				
				return wanded ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
			}
		} else {
			//Wand binding mechanics
			//Cancel a bind-in-progress by clicking the block again
			if(pos.equals(boundPos)) {
				setBoundTile(stack, UNBOUND_POS);
				return EnumActionResult.SUCCESS;
			}
			
			//Try to complete a binding
			if(boundPos.getY() != -1 && boundTile instanceof IWandBindable) {
				if(((IWandBindable) boundTile).bindTo(player, stack, pos, side)) {
					VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, boundPos);
					setBoundTile(stack, UNBOUND_POS);
					
					if(world.isRemote) {
						Vector3 orig = new Vector3(boundPos.getX() + 0.5, boundPos.getY() + 0.5, boundPos.getZ() + 0.5);
						Vector3 end = new Vector3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
						doParticleBeam(world, orig, end);
					}
					
					return EnumActionResult.SUCCESS;
				}
			}
			
			//Handle binding a force relay to something
			if(!world.isRemote && ((BlockPistonRelay) ModBlocks.pistonRelay).onPairCompleted(player, world, pos)) { 
				world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.ding, SoundCategory.PLAYERS, 1F, 1F);
				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}

	public static void doParticleBeam(World world, Vector3 orig, Vector3 end) {
		if(!world.isRemote)
			return;

		Vector3 diff = end.subtract(orig);
		Vector3 movement = diff.normalize().multiply(0.05);
		int iters = (int) (diff.mag() / movement.mag());
		float huePer = 1F / iters;
		float hueSum = (float) Math.random();

		Vector3 currentPos = orig;
		for(int i = 0; i < iters; i++) {
			float hue = i * huePer + hueSum;
			Color color = Color.getHSBColor(hue, 1F, 1F);
			float r = color.getRed() / 255F;
			float g = color.getGreen() / 255F;
			float b = color.getBlue() / 255F;

			Botania.proxy.setSparkleFXNoClip(true);
			Botania.proxy.sparkleFX(currentPos.x, currentPos.y, currentPos.z, r, g, b, 0.5F, 4);
			Botania.proxy.setSparkleFXNoClip(false);
			currentPos = currentPos.add(movement);
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		BlockPos coords = getBoundTile(stack);
		TileEntity tile = world.getTileEntity(coords);
		if(tile == null || !(tile instanceof IWandBindable))
			setBoundTile(stack, UNBOUND_POS);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(player.isSneaking() && world.isRemote) {
			player.playSound(ModSounds.ding, 0.1F, 1F);
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> stacks) {
		if(isInCreativeTab(tab)) {
			for(int i = 0; i < 16; i++)
				stacks.add(forColors(i, i));
		}
	}

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return getUnlocalizedNameLazy(par1ItemStack);
	}

	@Nonnull
	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return EnumRarity.RARE;
	}

	public static ItemStack forColors(int color1, int color2) {
		ItemStack stack = new ItemStack(ModItems.twigWand);
		ItemNBTHelper.setInt(stack, TAG_COLOR1, color1);
		ItemNBTHelper.setInt(stack, TAG_COLOR2, color2);

		return stack;
	}

	public static int getColor1(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_COLOR1, 0);
	}

	public static int getColor2(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_COLOR2, 0);
	}

	public static void setBoundTile(ItemStack stack, BlockPos pos) {
		ItemNBTHelper.setInt(stack, TAG_BOUND_TILE_X, pos.getX());
		ItemNBTHelper.setInt(stack, TAG_BOUND_TILE_Y, pos.getY());
		ItemNBTHelper.setInt(stack, TAG_BOUND_TILE_Z, pos.getZ());
	}

	public static BlockPos getBoundTile(ItemStack stack) {
		int x = ItemNBTHelper.getInt(stack, TAG_BOUND_TILE_X, 0);
		int y = ItemNBTHelper.getInt(stack, TAG_BOUND_TILE_Y, -1);
		int z = ItemNBTHelper.getInt(stack, TAG_BOUND_TILE_Z, 0);
		return new BlockPos(x, y, z);
	}

	@Override
	public BlockPos getBinding(ItemStack stack) {
		BlockPos bound = getBoundTile(stack);
		if(bound.getY() != -1)
			return bound;

		RayTraceResult pos = Minecraft.getMinecraft().objectMouseOver;
		if(pos != null && pos.typeOfHit == RayTraceResult.Type.BLOCK) {
			TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(pos.getBlockPos());
			if(tile != null && tile instanceof ITileBound) {
				BlockPos coords = ((ITileBound) tile).getBinding();
				return coords;
			}
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

}
