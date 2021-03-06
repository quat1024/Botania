/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

import org.apache.commons.lang3.mutable.MutableFloat;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.core.handler.ModSounds;

public class ItemBalanceCloak extends ItemHolyCloak {

	private static final Identifier texture = new Identifier(LibResources.MODEL_BALANCE_CLOAK);
	private static final Identifier textureGlow = new Identifier(LibResources.MODEL_BALANCE_CLOAK_GLOW);

	public ItemBalanceCloak(Settings props) {
		super(props);
	}

	@Override
	public boolean effectOnDamage(DamageSource src, MutableFloat amount, PlayerEntity player, ItemStack stack) {
		if (!src.getMagic()) {
			amount.setValue(amount.getValue() / 2);

			if (src.getAttacker() != null) {
				src.getAttacker().damage(DamageSource.magic(player, player), amount.getValue());
			}

			if (amount.getValue() > player.getHealth()) {
				amount.setValue(player.getHealth() - 1);
			}

			player.world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.holyCloak, SoundCategory.PLAYERS, 1F, 1F);
			for (int i = 0; i < 30; i++) {
				double x = player.getX() + Math.random() * player.getWidth() * 2 - player.getWidth();
				double y = player.getY() + Math.random() * player.getHeight();
				double z = player.getZ() + Math.random() * player.getWidth() * 2 - player.getWidth();
				boolean green = Math.random() > 0.5;
				float g = green ? 1F : 0.3F;
				float b = green ? 0.3F : 1F;
				SparkleParticleData data = SparkleParticleData.sparkle(0.8F + (float) Math.random() * 0.4F, 0.3F, g, b, 3);
				player.world.addParticle(data, x, y, z, 0, 0, 0);
			}
			return true;
		}

		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	Identifier getCloakTexture() {
		return texture;
	}

	@Override
	@Environment(EnvType.CLIENT)
	Identifier getCloakGlowTexture() {
		return textureGlow;
	}

}
