package com.radiance.mixin_related.extensions.vulkan_render_integration;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.VertexFormat;

public interface IShaderProgramExt {

    String neoVoxelRT$getShaderName();

    void neoVoxelRT$setShaderName(String shaderName);

    VertexFormat neoVoxelRT$getVertexFormat();

    void neoVoxelRT$setVertexFormat(VertexFormat vertexFormat);

    String neoVoxelRT$getVertexSource();

    void neoVoxelRT$setVertexSource(String vertexSource);

    String neoVoxelRT$getFragmentSource();

    void neoVoxelRT$setFragmentSource(String fragmentSource);

    List<String> neoVoxelRT$getSamplerNamesValue();

    List<GlUniform> neoVoxelRT$getUniformsValue();

    Object2IntMap<String> neoVoxelRT$getSamplerTexturesValue();
}
