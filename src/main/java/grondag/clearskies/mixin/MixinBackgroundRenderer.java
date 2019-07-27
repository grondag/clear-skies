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
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {
    @Redirect(method = "updateColorNotInWater", require = 1, at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/world/World;getFogColor(F)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d hookUpdateColorNotInWaterSky(World world, float tickDelta) {
        if(world.dimension.hasVisibleSky() && !world.isRaining()) {
            final MinecraftClient mc = MinecraftClient.getInstance();
            return world.getSkyColor(mc.gameRenderer.getCamera().getBlockPos(), tickDelta);
        } else {
            return world.getFogColor(tickDelta);
        }
    }
}
