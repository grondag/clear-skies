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

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public class MixinBackgroundRenderer {
	@Shadow private static float red;
	@Shadow private static float green;
	@Shadow private static float blue;
	@Shadow private static int waterFogColor = -1;
	@Shadow private static int nextWaterFogColor = -1;
	@Shadow private static long lastWaterFogColorUpdateTime = -1L;

	@ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/CubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/CubicSampler$RgbFetcher;)Lnet/minecraft/util/math/Vec3d;"), method = "render", ordinal = 2, require = 1, allow = 1)
	private static Vec3 onSampleColor(Vec3 val) {
		final Minecraft mc = Minecraft.getInstance();
		final ClientLevel world = mc.level;

		if (world.dimensionType().hasSkyLight()) {
			return world.getSkyColor(mc.gameRenderer.getMainCamera().getPosition(), mc.getFrameTime());
		} else {
			return val;
		}
	}

	@ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/Vec3f;dot(Lnet/minecraft/util/math/Vec3f;)F"), method = "render", ordinal = 7, require = 1, allow = 1)
	private static float afterPlaneDot(float dotPrduct) {
		return 0;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"), method = "render", require = 1, allow = 1)
	private static float onGetRainGradient(ClientLevel world, float tickDelta) {
		return 0;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getThunderGradient(F)F"), method = "render", require = 1, allow = 1)
	private static float onGetThunderGradient(ClientLevel world, float tickDelta) {
		return 0;
	}
}
