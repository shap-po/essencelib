package com.github.shap_po.essencelib.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

/**
 * Custom RenderLayers for Collector's Intuition glow.
 * Uses ALWAYS_DEPTH_TEST + COLOR_MASK so the glow is visible through blocks
 * without blocking the view.
 */
@Environment(EnvType.CLIENT)
public final class CollectorIntuitionRenderLayers {

    /** Halo layer: solid quads, translucent, see-through blocks, for scaled-up item glow. */
    public static final RenderLayer GLOW_HALO = RenderLayer.of(
        "essencelib_collector_glow_halo",
        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
        VertexFormat.DrawMode.QUADS,
        256,
        false,
        true,
        RenderLayer.MultiPhaseParameters.builder()
            .program(RenderPhase.ENTITY_TRANSLUCENT_CULL_PROGRAM)
            .texture(RenderPhase.MIPMAP_BLOCK_ATLAS_TEXTURE)
            .transparency(RenderPhase.NO_TRANSPARENCY)
            .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
            .cull(RenderPhase.ENABLE_CULLING)
            .lightmap(RenderPhase.ENABLE_LIGHTMAP)
            .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
            .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
            .target(RenderPhase.MAIN_TARGET)
            .texturing(RenderPhase.DEFAULT_TEXTURING)
            .writeMaskState(RenderPhase.COLOR_MASK)
            .build(false)
    );

    /** Lines layer: same as getLines() but with ALWAYS_DEPTH_TEST + COLOR_MASK so it doesn't block view. */
    public static final RenderLayer GLOW_LINES = RenderLayer.of(
        "essencelib_collector_glow_lines",
        VertexFormats.POSITION_COLOR,
        VertexFormat.DrawMode.LINES,
        256,
        false,
        false,
        RenderLayer.MultiPhaseParameters.builder()
            .program(RenderPhase.LINES_PROGRAM)
            .texture(RenderPhase.NO_TEXTURE)
            .transparency(RenderPhase.NO_TRANSPARENCY)
            .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
            .cull(RenderPhase.DISABLE_CULLING)
            .lightmap(RenderPhase.DISABLE_LIGHTMAP)
            .overlay(RenderPhase.DISABLE_OVERLAY_COLOR)
            .layering(RenderPhase.NO_LAYERING)
            .target(RenderPhase.MAIN_TARGET)
            .texturing(RenderPhase.DEFAULT_TEXTURING)
            .writeMaskState(RenderPhase.COLOR_MASK)
            .lineWidth(RenderPhase.FULL_LINE_WIDTH)
            .colorLogic(RenderPhase.NO_COLOR_LOGIC)
            .build(false)
    );

    private CollectorIntuitionRenderLayers() {}
}
