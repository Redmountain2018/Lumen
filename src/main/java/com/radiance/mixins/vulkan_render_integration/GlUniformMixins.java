package com.radiance.mixins.vulkan_render_integration;

import com.radiance.mixin_related.extensions.vulkan_render_integration.IGlUniformExt;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.gl.GlUniform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GlUniform.class)
public abstract class GlUniformMixins implements IGlUniformExt {

    @Shadow
    @Final
    private int count;

    @Shadow
    @Final
    private int dataType;

    @Shadow
    @Final
    private IntBuffer intData;

    @Shadow
    @Final
    private FloatBuffer floatData;

    @Override
    public int neoVoxelRT$getDataTypeValue() {
        return this.dataType;
    }

    @Override
    public int neoVoxelRT$getCountValue() {
        return this.count;
    }

    @Override
    public IntBuffer neoVoxelRT$getIntDataValue() {
        return this.intData;
    }

    @Override
    public FloatBuffer neoVoxelRT$getFloatDataValue() {
        return this.floatData;
    }
}
