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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {
	@ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/world/ClientWorld;getFogColor(F)Lnet/minecraft/util/math/Vec3d;"), method = "render", ordinal = 1, require = 1, allow = 1)
	private static Vec3d onGetFogColor(Vec3d val) {
		final MinecraftClient mc = MinecraftClient.getInstance();
		final ClientWorld world = mc.world;

		if (world.dimension.hasVisibleSky() && !world.isRaining()) {
			return world.method_23777(mc.gameRenderer.getCamera().getBlockPos(), mc.getTickDelta());
		} else {
			return val;
		}
	}
}
