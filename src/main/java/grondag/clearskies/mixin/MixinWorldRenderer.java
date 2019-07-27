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
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.render.WorldRenderer;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    @Redirect(method = "renderSky", require = 1, at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/client/gl/GlBuffer;draw(I)V"))
    private void hookBufferDraw(GlBuffer buffer, int primitive) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        if(!mc.world.dimension.hasVisibleSky() || mc.world.isRaining()) {
            buffer.draw(primitive);
        }
    }
}
