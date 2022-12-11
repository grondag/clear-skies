/*
 *
 * This file is part of Clear Skies and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package grondag.clearskies.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.phys.Vec3;

@Mixin(FogRenderer.class)
public class MixinFogRenderer {
	@ModifyVariable(
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"),
			method = "setupColor",
			ordinal = 2,
			require = 1,
			allow = 1)
	private static Vec3 onSampleColor(Vec3 val) {
		final Minecraft mc = Minecraft.getInstance();
		final ClientLevel world = mc.level;

		if (world.dimensionType().hasSkyLight()) {
			return world.getSkyColor(mc.gameRenderer.getMainCamera().getPosition(), mc.getFrameTime());
		} else {
			return val;
		}
	}

	@ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lorg/joml/Vector3f;dot(Lorg/joml/Vector3fc;)F"), method = "setupColor", ordinal = 7, require = 1, allow = 1)
	private static float afterPlaneDot(float dotPrduct) {
		return 0;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"), method = "setupColor", require = 1, allow = 1)
	private static float onGetRainLevel(ClientLevel world, float tickDelta) {
		return 0;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getThunderLevel(F)F"), method = "setupColor", require = 1, allow = 1)
	private static float onGetThunderLevel(ClientLevel world, float tickDelta) {
		return 0;
	}
}
