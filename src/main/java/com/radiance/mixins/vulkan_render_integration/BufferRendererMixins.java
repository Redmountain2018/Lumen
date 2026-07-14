package com.radiance.mixins.vulkan_render_integration;

import com.mojang.blaze3d.systems.RenderSystem;
import com.radiance.client.constant.Constants;
import com.radiance.client.proxy.vulkan.BufferProxy;
import com.radiance.client.proxy.vulkan.RendererProxy;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BufferRenderer.class)
public class BufferRendererMixins {

    /**
     * Maps Constants.VertexFormats value → OverlayDrawPipelineType (from ui_module.hpp).
     * OverlayDrawPipelineType enum values:
     *   POSITION_TEX=0, POSITION_COLOR=1, POSITION_TEX_COLOR=2,
     *   POSITION_COLOR_TEX_LIGHT=3, POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL=4,
     *   POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL_NO_OUTLINE=5,
     *   POSITION_END_PORTAL=6, POSITION=7
     */
    @Unique
    private static final int[] VERTEX_FORMAT_TO_PIPELINE_TYPE = {
        3,  // 0: POSITION_COLOR_TEXTURE_LIGHT_NORMAL → POSITION_COLOR_TEX_LIGHT
        4,  // 1: POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL → POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
        3,  // 2: POSITION_TEXTURE_COLOR_LIGHT → POSITION_COLOR_TEX_LIGHT
        7,  // 3: POSITION → POSITION
        1,  // 4: POSITION_COLOR → POSITION_COLOR
        1,  // 5: LINES → POSITION_COLOR (best fit)
        3,  // 6: POSITION_COLOR_LIGHT → POSITION_COLOR_TEX_LIGHT
        0,  // 7: POSITION_TEXTURE → POSITION_TEX
        2,  // 8: POSITION_TEXTURE_COLOR → POSITION_TEX_COLOR
        3,  // 9: POSITION_COLOR_TEXTURE_LIGHT → POSITION_COLOR_TEX_LIGHT
        3,  // 10: POSITION_TEXTURE_LIGHT_COLOR → POSITION_COLOR_TEX_LIGHT
        2,  // 11: POSITION_TEXTURE_COLOR_NORMAL → POSITION_TEX_COLOR (best fit)
        3,  // 12: PBR_TRIANGLE → POSITION_COLOR_TEX_LIGHT (best fit)
    };

    @Unique
    private static int getOverlayPipelineType(VertexFormat vertexFormat) {
        try {
            int formatValue = Constants.VertexFormats.getValue(vertexFormat);
            if (formatValue >= 0 && formatValue < VERTEX_FORMAT_TO_PIPELINE_TYPE.length) {
                return VERTEX_FORMAT_TO_PIPELINE_TYPE[formatValue];
            }
        } catch (Exception e) {
            // fallback
        }
        return 0; // POSITION_TEX (fallback)
    }

    @Inject(method = "drawWithGlobalProgram(Lnet/minecraft/client/render/BuiltBuffer;)V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;assertOnRenderThread()V", shift = At.Shift.AFTER, remap = false),
        cancellable = true)
    private static void rewriteDrawWithGlobalProgram(BuiltBuffer buffer, CallbackInfo ci) {
        ShaderProgram shaderProgram = RenderSystem.getShader();
        if (shaderProgram == null) {
            buffer.close();
            ci.cancel();
            return;
        }
        BufferProxy.updateOverlayDrawUniform();
        BufferProxy.VertexIndexBufferHandle handle = BufferProxy.createAndUploadVertexIndexBuffer(
            buffer);
        //BufferProxy.performQueuedUpload();
        int pipelineType = getOverlayPipelineType(buffer.getDrawParameters().format());
        RendererProxy.bindOverlayPipeline(pipelineType);
        RendererProxy.drawOverlay(handle,
            buffer.getDrawParameters()
                .indexCount(),
            buffer.getDrawParameters()
                .indexType());
        buffer.close();
        ci.cancel();
    }
}
