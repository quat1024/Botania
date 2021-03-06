/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.mixin;

import net.minecraft.entity.projectile.thrown.ThrownEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import vazkii.botania.common.entity.EntityManaBurst;

@Mixin(ThrownEntity.class)
public class MixinThrowableEntity {
	@ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(D)Lnet/minecraft/util/math/Vec3d;"))
	private double noDrag(double origScale) {
		// Do not apply drag to bursts
		if ((Object) this instanceof EntityManaBurst) {
			return 1;
		}
		return origScale;
	}
}
