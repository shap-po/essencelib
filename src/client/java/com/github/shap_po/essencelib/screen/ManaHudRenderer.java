package com.github.shap_po.essencelib.screen;

import com.github.shap_po.essencelib.client.ClientManaData;
import com.github.shap_po.essencelib.component.DownedComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

public class ManaHudRenderer implements HudRenderCallback {
    private static final int BAR_WIDTH = 182;              // Match vanilla XP width
    private static final int MANA_HEIGHT = 3;              // 2-3px peek above XP
    private static final int XP_BASE_Y_OFFSET = 32;        // Vanilla XP baseline
    private static final int ABOVE_XP_GAP = 0;             // Tucked directly against XP
    // Position controls: tweak these two values only.
    private static final int OFFSET_X = 0;                 // -left, +right
    private static final int OFFSET_Y = 10;                 // -up, +down

    private static final float PULSE_SPEED = 0.015f;
    private static final float GRADIENT_SPEED_1 = 0.0010f;
    private static final float GRADIENT_SPEED_2 = 0.0020f;
    private static final float GRADIENT_SPEED_3 = 0.0035f;

    // Minimal look: thin animated line + subtle edge accents.
    private static final int TRACK_BACKGROUND = 0x2810182A;
    private static final int EDGE_DARK = 0xB01C2840;
    private static final int EDGE_BRIGHT = 0xD090C8FF;

    private static final int MANA_1_START = 0xD01E8FFF;
    private static final int MANA_1_MID = 0xD02F73FF;
    private static final int MANA_1_END = 0xD01A47E0;

    private static final int MANA_2_START = 0xA000F4FF;
    private static final int MANA_2_MID = 0xA000CFFF;
    private static final int MANA_2_END = 0xA000F4FF;

    private static final int MANA_3_START = 0x45FFFFFF;
    private static final int MANA_3_MID = 0x4500FFFF;
    private static final int MANA_3_END = 0x45FFFFFF;

    private static final int GLOW_COLOR_BRIGHT = 0xB0FFFFFF;
    private static final int GLOW_COLOR_DIM = 0x7000E8FF;

    private float gradientOffset1 = 0f;
    private float gradientOffset2 = 0f;
    private float gradientOffset3 = 0f;

    // Add back the pulse variables
    private float pulseProgress = 0;
    private boolean pulseDirection = true;
    private float previousMana = 0;
    private float previousMax = 0;

    private static final float WAVE_AMPLITUDE_1 = 0.045f;
    private static final float WAVE_AMPLITUDE_2 = 0.030f;
    private static final float WAVE_AMPLITUDE_3 = 0.016f;
    private static final float WAVE_FREQUENCY_1 = 5.2f;
    private static final float WAVE_FREQUENCY_2 = 7.4f;
    private static final float WAVE_FREQUENCY_3 = 10.4f;

    private int currentMana1Start = MANA_1_START;
    private int currentMana2Start = MANA_2_START;
    private int currentMana3Start = MANA_3_START;

    @Override
    public void onHudRender(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        if (client.options.hudHidden) return;
        if (DownedComponent.isDowned(player)) return;
        
        // Don't render mana bar in creative mode
        if (player.getAbilities().creativeMode) return;

        ClientManaData.tick();

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        
        float current = ClientManaData.getCurrentMana();
        float max = ClientManaData.getMaxMana();
        float manaRatio = Math.min(1.0f, max > 0 ? current / max : 0);

        // Keep the mana line directly above vanilla XP (not high on the HUD).
        int x = width / 2 - BAR_WIDTH / 2 + OFFSET_X;
        int xpY = height - XP_BASE_Y_OFFSET;
        int y = xpY - MANA_HEIGHT - ABOVE_XP_GAP + OFFSET_Y;
        if (y < 4) {
            y = 4;
        }

        // Add back pulse animation before gradient updates
        pulseProgress += (pulseDirection ? 0.01f : -0.01f);
        if (pulseProgress >= 1.0f) pulseDirection = false;
        if (pulseProgress <= 0.3f) pulseDirection = true;

        // Update the three gradient positions
        gradientOffset1 = (gradientOffset1 + GRADIENT_SPEED_1) % 1.0f;
        gradientOffset2 = (gradientOffset2 + GRADIENT_SPEED_2) % 1.0f;
        gradientOffset3 = (gradientOffset3 + GRADIENT_SPEED_3) % 1.0f;

        // Minimal track with only edge details.
        context.fill(x, y, x + BAR_WIDTH, y + MANA_HEIGHT, TRACK_BACKGROUND);
        context.fill(x - 1, y, x, y + MANA_HEIGHT, EDGE_DARK);
        context.fill(x + BAR_WIDTH, y, x + BAR_WIDTH + 1, y + MANA_HEIGHT, EDGE_DARK);
        context.fill(x - 1, y, x, y + 1, EDGE_BRIGHT);
        context.fill(x + BAR_WIDTH, y, x + BAR_WIDTH + 1, y + 1, EDGE_BRIGHT);

        // --- MANA BAR ---
        if (manaRatio > 0) {
            int filledWidth = (int)(BAR_WIDTH * manaRatio);
            
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
                
                // 3px stacked shading: glossy top, vivid core, deeper base.
                int topColor = interpolateColor(finalColor, 0xD0E8FFFF, 0.35f);
                int coreColor = finalColor;
                int baseColor = interpolateColor(finalColor, 0xB000163A, 0.45f);

                context.fill(x + i, y, x + i + 1, y + 1, topColor);
                if (MANA_HEIGHT > 2) {
                    context.fill(x + i, y + 1, x + i + 1, y + MANA_HEIGHT - 1, coreColor);
                }
                context.fill(x + i, y + MANA_HEIGHT - 1, x + i + 1, y + MANA_HEIGHT, baseColor);

                // Tiny traveling sparkle for extra life in thin space.
                if (((i + (int)(gradientOffset3 * 1200)) % 17) == 0) {
                    context.fill(x + i, y, x + i + 1, y + 1, 0xD0FFFFFF);
                }
            }

            // Subtle aura line above the strip, tied to pulse.
            int auraColor = interpolateColor(0x3000E8FF, 0x12008CD0, pulseProgress);
            context.fill(x, y - 1, x + filledWidth, y, auraColor);
            
            boolean isIncreasing = current > previousMana || (max > previousMax && current >= previousMana);
            if (isIncreasing && manaRatio < 0.99f) {
                pulseProgress += (pulseDirection ? PULSE_SPEED : -PULSE_SPEED);
                if (pulseProgress >= 1.0f) pulseDirection = false;
                if (pulseProgress <= 0.2f) pulseDirection = true;

                int tipGlow = interpolateColor(GLOW_COLOR_BRIGHT, GLOW_COLOR_DIM, pulseProgress);
                int outerGlow = interpolateColor(GLOW_COLOR_DIM, 0x00000000, pulseProgress);
                
                int glowX = x + Math.max(0, filledWidth - 1);
                int glowY = y + (MANA_HEIGHT / 2);
                
                context.fill(glowX, glowY, glowX + 1, glowY + 1, tipGlow);
                context.fill(glowX - 1, glowY, glowX, glowY + 1, outerGlow);
                context.fill(glowX + 1, glowY, glowX + 2, glowY + 1, outerGlow);
            }

            previousMana = current;
            previousMax = max;

            if (manaRatio > 0.99f) {
                float fullPulse = (float)(Math.sin(gradientOffset1 * Math.PI * 2) * 0.1 + 0.9);
                currentMana1Start = adjustColorAlpha(MANA_1_START, fullPulse);
                currentMana2Start = adjustColorAlpha(MANA_2_START, fullPulse);
                currentMana3Start = adjustColorAlpha(MANA_3_START, fullPulse);
            }
        } else {
            previousMana = current;
            previousMax = max;
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
