package com.github.shap_po.essencelib.screen;

import com.github.shap_po.essencelib.client.ClientManaData;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ManaHudRenderer implements HudRenderCallback {
    private static final int BAR_WIDTH = 182;  // Match experience bar width
    private static final int BAR_HEIGHT = 3;   // Make it thinner to fit
    
    // Modify pulse speeds for slower animation
    private static final float PULSE_SPEED = 0.015f;         // Much slower pulse (was 0.035f)
    private static final float GRADIENT_SPEED_1 = 0.001f;    // Slower ethereal base (was 0.002f)
    private static final float GRADIENT_SPEED_2 = 0.002f;    // Slower magical flow (was 0.004f)
    private static final float GRADIENT_SPEED_3 = 0.004f;    // Slower sparkles (was 0.008f)
    
    // More magical color scheme with varying transparency
    private static final int BACKGROUND = 0x15101820;  // Even more transparent, slight blue tint
    private static final int BORDER_COLOR = 0x20000060;  // Slightly more visible border with purple tint
    
    // First gradient layer (deep, slow-moving base)
    private static final int MANA_1_START = 0x501E88E5;  // Increased opacity for base
    private static final int MANA_1_MID = 0x502B4FD1;    // More vibrant purple
    private static final int MANA_1_END = 0x501A237E;    // Deep magical purple
    
    // Second gradient layer (bright magical flow)
    private static final int MANA_2_START = 0x4000FFFF;  // Brighter cyan
    private static final int MANA_2_MID = 0x4000BFFF;    // Sky blue
    private static final int MANA_2_END = 0x4000FFFF;    // Back to cyan
    
    // Third gradient layer (sparkly highlights)
    private static final int MANA_3_START = 0x25FFFFFF;  // Brighter sparkles
    private static final int MANA_3_MID = 0x2500FFFF;    // Cyan sparkle
    private static final int MANA_3_END = 0x25FFFFFF;    // Back to white
    
    // Modify glow constants
    private static final int GLOW_COLOR_BRIGHT = 0x90FFFFFF;  // Brighter center
    private static final int GLOW_COLOR_DIM = 0x5000FFFF;    // More visible outer glow
    private static final int GLOW_SIZE = 2;                  // Size of the glow effect

    private float gradientOffset1 = 0f;
    private float gradientOffset2 = 0f;
    private float gradientOffset3 = 0f;

    // Add back the pulse variables
    private float pulseProgress = 0;
    private boolean pulseDirection = true;
    private float previousMana = 0;
    private float previousMax = 0;

    // Add wave constants
    private static final float WAVE_AMPLITUDE_1 = 0.04f;  // Slightly reduced for subtlety
    private static final float WAVE_AMPLITUDE_2 = 0.03f;
    private static final float WAVE_AMPLITUDE_3 = 0.015f; // More subtle sparkles
    private static final float WAVE_FREQUENCY_1 = 5.0f;   // Slower waves
    private static final float WAVE_FREQUENCY_2 = 7.0f;   // Medium waves
    private static final float WAVE_FREQUENCY_3 = 11.0f;  // Fast sparkles

    // Add these non-final variables for dynamic colors
    private int currentMana1Start = MANA_1_START;
    private int currentMana2Start = MANA_2_START;
    private int currentMana3Start = MANA_3_START;

    @Override
    public void onHudRender(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        // Don't render mana bar in creative mode
        if (client.player.getAbilities().creativeMode) return;

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        
        float current = ClientManaData.getCurrentMana();
        float max = ClientManaData.getMaxMana();
        float ratio = Math.min(1.0f, max > 0 ? current / max : 0);

        // Adjust Y position to be lower, closer to hotbar
        int x = width / 2 - BAR_WIDTH / 2;
        int y = height - 30;  // Move closer to hotbar (was -32)

        // Add back pulse animation before gradient updates
        pulseProgress += (pulseDirection ? 0.01f : -0.01f);  // Slower pulse (was 0.02f)
        if (pulseProgress >= 1.0f) pulseDirection = false;
        if (pulseProgress <= 0.3f) pulseDirection = true;

        // Update the three gradient positions
        gradientOffset1 = (gradientOffset1 + GRADIENT_SPEED_1) % 1.0f;
        gradientOffset2 = (gradientOffset2 + GRADIENT_SPEED_2) % 1.0f;
        gradientOffset3 = (gradientOffset3 + GRADIENT_SPEED_3) % 1.0f;

        // Draw background
        context.fill(x + 1, y - 1, x + BAR_WIDTH - 1, y, BORDER_COLOR); // Top
        context.fill(x + 1, y + BAR_HEIGHT, x + BAR_WIDTH - 1, y + BAR_HEIGHT + 1, BORDER_COLOR); // Bottom
        context.fill(x - 1, y + 1, x, y + BAR_HEIGHT - 1, BORDER_COLOR); // Left
        context.fill(x + BAR_WIDTH, y + 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT - 1, BORDER_COLOR); // Right

        // Rounded corners
        context.fill(x, y, x + 1, y + 1, BORDER_COLOR); // Top-left
        context.fill(x + BAR_WIDTH - 1, y, x + BAR_WIDTH, y + 1, BORDER_COLOR); // Top-right
        context.fill(x, y + BAR_HEIGHT - 1, x + 1, y + BAR_HEIGHT, BORDER_COLOR); // Bottom-left
        context.fill(x + BAR_WIDTH - 1, y + BAR_HEIGHT - 1, x + BAR_WIDTH, y + BAR_HEIGHT, BORDER_COLOR); // Bottom-right

        context.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, BACKGROUND);

        if (ratio > 0) {
            int filledWidth = (int)(BAR_WIDTH * ratio);
            
            for (int i = 0; i < filledWidth; i++) {
                float pos = (float)i / BAR_WIDTH;
                
                // Add sine waves to each gradient layer
                float wave1 = (float)Math.sin(pos * WAVE_FREQUENCY_1 + gradientOffset1 * Math.PI * 2) * WAVE_AMPLITUDE_1;
                float wave2 = (float)Math.sin(pos * WAVE_FREQUENCY_2 + gradientOffset2 * Math.PI * 2) * WAVE_AMPLITUDE_2;
                float wave3 = (float)Math.sin(pos * WAVE_FREQUENCY_3 + gradientOffset3 * Math.PI * 2) * WAVE_AMPLITUDE_3;
                
                float segment1 = ((pos + wave1 + gradientOffset1) % 1.0f);
                float segment2 = ((pos + wave2 + gradientOffset2) % 1.0f);
                float segment3 = ((pos + wave3 + gradientOffset3) % 1.0f);
                
                // Calculate colors for each layer
                int color1 = segment1 < 0.5f ? 
                    interpolateColor(currentMana1Start, MANA_1_MID, segment1 * 2) :
                    interpolateColor(MANA_1_MID, MANA_1_END, (segment1 - 0.5f) * 2);
                    
                int color2 = segment2 < 0.5f ?
                    interpolateColor(currentMana2Start, MANA_2_MID, segment2 * 2) :
                    interpolateColor(MANA_2_MID, MANA_2_END, (segment2 - 0.5f) * 2);
                    
                int color3 = segment3 < 0.5f ?
                    interpolateColor(currentMana3Start, MANA_3_MID, segment3 * 2) :
                    interpolateColor(MANA_3_MID, MANA_3_END, (segment3 - 0.5f) * 2);
                
                // Blend all three colors
                int finalColor = blendColors(color1, color2, color3);
                
                if (i == 0 || i == filledWidth - 1) {
                    context.fill(x + i, y + 1, x + i + 1, y + BAR_HEIGHT - 1, finalColor);
                } else {
                    context.fill(x + i, y, x + i + 1, y + BAR_HEIGHT, finalColor);
                }
            }
            
            // Add back the glow effect for regeneration
            boolean isIncreasing = current > previousMana || (max > previousMax && current >= previousMana);
            if (isIncreasing && ratio < 0.99f) {
                // Brighter pulse effect
                pulseProgress += (pulseDirection ? PULSE_SPEED : -PULSE_SPEED);
                if (pulseProgress >= 1.0f) pulseDirection = false;
                if (pulseProgress <= 0.2f) pulseDirection = true;

                // Pulse between bright white and dim cyan
                int tipGlow = interpolateColor(GLOW_COLOR_BRIGHT, GLOW_COLOR_DIM, pulseProgress);
                int outerGlow = interpolateColor(GLOW_COLOR_DIM, 0x00000000, pulseProgress); // Fade to transparent
                
                // Small circular glow effect with outward radiation
                int glowX = x + filledWidth;
                int glowY = y + (BAR_HEIGHT / 2); // Center vertically
                
                // Center dot
                context.fill(glowX, glowY, glowX + 1, glowY + 1, tipGlow);
                
                // Outer glow (radiating effect)
                context.fill(glowX - 1, glowY, glowX, glowY + 1, outerGlow); // Left
                context.fill(glowX + 1, glowY, glowX + 2, glowY + 1, outerGlow); // Right
                context.fill(glowX, glowY - 1, glowX + 1, glowY, outerGlow); // Top
                context.fill(glowX, glowY + 1, glowX + 1, glowY + 2, outerGlow); // Bottom
            }

            // Update previous values
            previousMana = current;
            previousMax = max;

            // Add subtle pulsing to the entire bar when full
            if (ratio > 0.99f) {
                float fullPulse = (float)(Math.sin(gradientOffset1 * Math.PI * 2) * 0.1 + 0.9);
                currentMana1Start = adjustColorAlpha(MANA_1_START, fullPulse);
                currentMana2Start = adjustColorAlpha(MANA_2_START, fullPulse);
                currentMana3Start = adjustColorAlpha(MANA_3_START, fullPulse);
            }
        }
    }

    private int interpolateColor(int startColor, int endColor, float ratio) {
        int a = (int)(((startColor >> 24) & 0xFF) + (((endColor >> 24) & 0xFF) - ((startColor >> 24) & 0xFF)) * ratio);
        int r = (int)(((startColor >> 16) & 0xFF) + (((endColor >> 16) & 0xFF) - ((startColor >> 16) & 0xFF)) * ratio);
        int g = (int)(((startColor >> 8) & 0xFF) + (((endColor >> 8) & 0xFF) - ((startColor >> 8) & 0xFF)) * ratio);
        int b = (int)((startColor & 0xFF) + ((endColor & 0xFF) - (startColor & 0xFF)) * ratio);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    // Add this new method for blending multiple colors with alpha
    private int blendColors(int... colors) {
        float totalA = 0, totalR = 0, totalG = 0, totalB = 0;
        
        for (int color : colors) {
            float a = ((color >> 24) & 0xFF) / 255f;
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            
            totalA += a;
            totalR += r * a;
            totalG += g * a;
            totalB += b * a;
        }
        
        totalA = Math.min(1.0f, totalA);
        if (totalA > 0) {
            totalR /= totalA;
            totalG /= totalA;
            totalB /= totalA;
        }
        
        return ((int)(totalA * 255) << 24) |
               ((int)(totalR * 255) << 16) |
               ((int)(totalG * 255) << 8) |
               ((int)(totalB * 255));
    }

    // Add this helper method
    private int adjustColorAlpha(int color, float factor) {
        int alpha = (int)(((color >> 24) & 0xFF) * factor);
        return (alpha << 24) | (color & 0x00FFFFFF);
    }
} 
