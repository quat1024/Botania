/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.brew.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

import vazkii.botania.common.brew.ModPotions;

public class PotionSoulCross extends StatusEffect {

	public PotionSoulCross() {
		super(StatusEffectType.BENEFICIAL, 0x47453d);
	}

	public static void onEntityKill(LivingEntity dying, LivingEntity killer) {
		if (killer.hasStatusEffect(ModPotions.soulCross)) {
			killer.heal(dying.getMaxHealth() / 20);
		}
	}

}
