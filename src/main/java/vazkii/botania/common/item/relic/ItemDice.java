/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.relic;

import net.minecraft.advancement.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.world.World;

import vazkii.botania.api.item.IRelic;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

public class ItemDice extends ItemRelic {
	public ItemDice(Settings props) {
		super(props);
	}

	public static Item[] getRelics() {
		return new Item[] {
				ModItems.infiniteFruit,
				ModItems.kingKey,
				ModItems.flugelEye,
				ModItems.thorRing,
				ModItems.odinRing,
				ModItems.lokiRing
		};
	}

	@Nonnull
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		if (isRightPlayer(player, stack)) {
			if (world.isClient) {
				return TypedActionResult.success(stack);
			}

			world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

			List<Integer> possible = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				if (!hasRelicAlready(player, i)) {
					possible.add(i);
				}
			}

			if (possible.isEmpty()) {
				player.sendSystemMessage(new TranslatableText("botaniamisc.dudDiceRoll", world.random.nextInt(6) + 1).formatted(Formatting.DARK_GREEN), Util.NIL_UUID);
				stack.decrement(1);
				return TypedActionResult.success(stack);
			} else {
				int relic = possible.get(world.random.nextInt(possible.size()));
				player.sendSystemMessage(new TranslatableText("botaniamisc.diceRoll", relic + 1).formatted(Formatting.DARK_GREEN), Util.NIL_UUID);
				return TypedActionResult.success(new ItemStack(getRelics()[relic]));
			}
		}

		return TypedActionResult.pass(stack);
	}

	@Override
	public boolean shouldDamageWrongPlayer() {
		return false;
	}

	private boolean hasRelicAlready(PlayerEntity player, int relic) {
		if (relic < 0 || relic > 6 || !(player instanceof ServerPlayerEntity)) {
			return true;
		}

		ServerPlayerEntity mpPlayer = (ServerPlayerEntity) player;
		Item item = getRelics()[relic];
		Identifier advId = ((IRelic) item).getAdvancement();

		if (advId != null) {
			Advancement adv = player.world.getServer().getAdvancementLoader().get(advId);
			return adv != null && mpPlayer.getAdvancementTracker().getProgress(adv).isDone();
		}

		return false;
	}

}
