/*******************************************************************************
 * Copyright 2019 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

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

	@ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lcom/mojang/math/Vector3f;dot(Lcom/mojang/math/Vector3f;)F"), method = "setupColor", ordinal = 7, require = 1, allow = 1)
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
