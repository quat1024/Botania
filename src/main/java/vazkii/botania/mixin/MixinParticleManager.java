package vazkii.botania.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.client.fx.FXSparkle;
import vazkii.botania.client.fx.FXWisp;

import java.util.List;

@Mixin(ParticleManager.class)
public class MixinParticleManager {
	@Shadow private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void addTypes(ClientWorld world, TextureManager textureManager, CallbackInfo ci) {
		PARTICLE_TEXTURE_SHEETS = ImmutableList.<ParticleTextureSheet>builder().addAll(PARTICLE_TEXTURE_SHEETS)
			.add(FXWisp.NORMAL_RENDER, FXWisp.DIW_RENDER)
			.add(FXSparkle.NORMAL_RENDER, FXSparkle.CORRUPT_RENDER)
			.build();
	}
}
