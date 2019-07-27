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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {
    
    //UGLY: only purpose is to force refmap output
    @SuppressWarnings("unused")
    @Inject(method = "updateColorNotInWater", at = @At("HEAD"), cancellable = false, require = 1)
    public void hookUpdateColorNotInWaterHead(Camera camera, World world, float tickDelta, CallbackInfo ci) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        if(world.dimension.hasVisibleSky() && !world.isRaining() && 0 == 1) {
            world.getSkyColor(mc.gameRenderer.getCamera().getBlockPos(), mc.getTickDelta());
        }
    }    
    
    @ModifyVariable(
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/World;getFogColor(F)Lnet/minecraft/util/math/Vec3d;"
                    ),
            method = "updateColorNotInWater",
            ordinal = 1,
            require = 1,
            allow = 1
            )
    private Vec3d hookUpdateColorNotInWaterSky(Vec3d val) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        final World world = mc.world;
        if(world.dimension.hasVisibleSky() && !world.isRaining()) {
            return world.getSkyColor(mc.gameRenderer.getCamera().getBlockPos(), mc.getTickDelta());
        } else {
            return val;
        }
    }
}
