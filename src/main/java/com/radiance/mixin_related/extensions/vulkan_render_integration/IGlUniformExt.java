package com.radiance.mixin_related.extensions.vulkan_render_integration;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface IGlUniformExt {

    int neoVoxelRT$getDataTypeValue();

    int neoVoxelRT$getCountValue();

    IntBuffer neoVoxelRT$getIntDataValue();

    FloatBuffer neoVoxelRT$getFloatDataValue();
}
