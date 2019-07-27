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

import java.nio.FloatBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.GlStateManager;

import grondag.clearskies.SkyboxState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(BackgroundRenderer.class)
public abstract class MixinBackgroundRenderer {
    @Shadow private float red;
    @Shadow private float green;
    @Shadow private float blue;
    
    @Shadow 
    protected abstract void updateColorNotInWater(Camera camera, World world, float tickDelta);
    
    private float fogRed;
    private float fogGreen;
    private float fogBlue;
    
    private float saveRed;
    private float saveGreen;
    private float saveBlue;
    
    private boolean hasFog = false;
    private boolean fogPass = false;
    
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = false, require = 1)
    private void renderBackgroundHead(Camera camera,float tickDelta, CallbackInfo ci) {
        if(!fogPass) {
            final World world = MinecraftClient.getInstance().world;
            if(world.dimension.hasVisibleSky() && !world.isRaining()) {
                fogPass = true;
                ((BackgroundRenderer)(Object)this).renderBackground(camera, tickDelta);
                fogPass = false;
                fogRed = red;
                fogGreen = green;
                fogBlue = blue;
                hasFog = true;
            } else {
                hasFog = false;
            }
        }
    }
    
    @Inject(method = "renderBackground", at = @At("RETURN"), cancellable = false, require = 1)
    private void renderBackgroundReturn(Camera camera,float tickDelta, CallbackInfo ci) {
        if(!fogPass && hasFog) {
            fogRed = (red + fogRed) * 0.5f;
            fogGreen = (green + fogGreen) * 0.5f;
            fogBlue = (blue + fogBlue) * 0.5f;
        }
    }
    
    @Redirect(method = "updateColorNotInWater", require = 1, at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/world/World;getFogColor(F)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d onpdateColorNotInWater(World world, float tickDelta) {
        if(fogPass) {
            return world.getFogColor(tickDelta);
        } else {
            final MinecraftClient mc = MinecraftClient.getInstance();
            return world.getSkyColor(mc.gameRenderer.getCamera().getBlockPos(), tickDelta);
        }
    }
    
    @Redirect(method = "renderBackground", require = 1, at = @At(value = "INVOKE", 
            target = "Lcom/mojang/blaze3d/platform/GlStateManager;clearColor(FFFF)V"))
    private void onClearColor(float r, float g, float b, float a) {
        if(!fogPass) {
            GlStateManager.clearColor(r, g, b, a);
        }
    }
    
    @Inject(method = "getColorAsBuffer", at = @At("HEAD"), cancellable = false, require = 1)
    private void getColorAsBufferHead(CallbackInfoReturnable<FloatBuffer> ci) {
        if(hasFog && !SkyboxState.isSkybox) {
            saveRed = red;
            saveGreen = green;
            saveBlue = blue;
            red = fogRed;
            green = fogGreen;
            blue = fogBlue;
        }
    }    
    
    @Inject(method = "getColorAsBuffer", at = @At("RETURN"), cancellable = false, require = 1)
    private void getColorAsBufferReturn (CallbackInfoReturnable<FloatBuffer> ci) {
        if(hasFog && !SkyboxState.isSkybox) {
            red = saveRed;
            green = saveGreen;
            blue = saveBlue;
        }
    }
}
