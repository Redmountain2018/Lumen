package com.radiance.client.proxy.world;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.render.chunk.SectionBuilder;

public final class ChunkOccupancyRegistry {

    private static final Map<SectionBuilder.RenderData, ChunkOccupancyData> DATA =
        Collections.synchronizedMap(new WeakHashMap<>());

    private ChunkOccupancyRegistry() {
    }

    public static void put(SectionBuilder.RenderData renderData, ChunkOccupancyData occupancyData) {
        DATA.put(renderData, occupancyData);
    }

    public static ChunkOccupancyData take(SectionBuilder.RenderData renderData) {
        return DATA.remove(renderData);
    }
}
