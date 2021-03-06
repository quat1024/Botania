package vazkii.botania.mixin;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.item.ItemKeepIvy;
import vazkii.botania.common.item.equipment.bauble.ItemFlightTiara;
import vazkii.botania.common.item.equipment.bauble.ItemTravelBelt;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
	@Inject(at = @At("HEAD"), method = "remove")
	private void onLogout(ServerPlayerEntity player, CallbackInfo ci) {
		ItemTravelBelt.playerLoggedOut(player);
		ItemFlightTiara.playerLoggedOut(player);
	}

	@ModifyVariable(at = @At("RETURN"), method = "respawnPlayer", ordinal = 2)
	private ServerPlayerEntity onRespawn(ServerPlayerEntity newPlayer, ServerPlayerEntity oldPlayer, boolean fromDeath) {
		if (!fromDeath) {
			ItemKeepIvy.onPlayerRespawn(newPlayer);
		}
		return newPlayer;
	}
}
