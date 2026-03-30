package com.radiance.mixins.vulkan_render_integration;

import com.radiance.client.option.Options;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixins {

    @Inject(method = "render", at = @At("TAIL"))
    private void renderFpsOverlay(DrawContext context, RenderTickCounter tickCounter,
        CallbackInfo ci) {
        if (!Options.showFpsOverlay) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.textRenderer == null) {
            return;
        }

        Text text = Text.translatable("radiance.overlay.fps", client.getCurrentFps());
        int x = 4;
        int y = 4;
        int width = client.textRenderer.getWidth(text);
        context.fill(x - 2, y - 2, x + width + 2, y + client.textRenderer.fontHeight + 2,
            0x90000000);
        context.drawTextWithShadow(client.textRenderer, text, x, y, 0xFFFFFF);
    }
}
