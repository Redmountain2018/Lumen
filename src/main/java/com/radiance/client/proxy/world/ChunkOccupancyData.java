package com.radiance.client.proxy.world;

public record ChunkOccupancyData(int[] waterOccupancy,
                                 int[] solidOccupancy,
                                 int sizeX,
                                 int sizeY,
                                 int sizeZ) {
}
