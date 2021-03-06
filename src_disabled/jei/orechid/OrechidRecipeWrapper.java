/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.integration.jei.orechid;

import javax.annotation.Nonnull;
import net.minecraft.util.Identifier;
import java.util.Map;

// This only exists to hold the map entry
public class OrechidRecipeWrapper implements Comparable<OrechidRecipeWrapper> {
	public final Map.Entry<Identifier, Integer> entry;

	public OrechidRecipeWrapper(Map.Entry<Identifier, Integer> entry) {
		this.entry = entry;
	}

	@Override
	public int compareTo(@Nonnull OrechidRecipeWrapper o) {
		return Integer.compare(o.entry.getValue(), entry.getValue());
	}
}
