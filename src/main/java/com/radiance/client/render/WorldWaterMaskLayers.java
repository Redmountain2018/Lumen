package com.radiance.client.render;

import net.minecraft.client.render.RenderLayer;

public final class WorldWaterMaskLayers {

    private static RenderLayer worldWaterMaskLayer;

    private WorldWaterMaskLayers() {
    }

    public static void setWorldWaterMaskLayer(RenderLayer layer) {
        worldWaterMaskLayer = layer;
    }

    public static RenderLayer getWorldWaterMaskLayer() {
        return worldWaterMaskLayer;
    }
}
