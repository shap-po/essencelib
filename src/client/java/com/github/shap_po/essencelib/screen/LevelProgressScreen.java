package com.github.shap_po.essencelib.screen;

import com.github.shap_po.essencelib.component.LevelComponent;
import com.github.shap_po.essencelib.level.LevelManager;
import com.github.shap_po.essencelib.util.MobEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A scrollable screen showing all killable mobs (alphabetically) in a 6-column grid.
 * Killed mobs show name + count; unkilled show a dark silhouette.
 */
@Environment(EnvType.CLIENT)
public class LevelProgressScreen extends Screen {

    private static final int COLS = 4;
    private static final int CELL_SIZE = 76;
    private static final int ROW_HEIGHT = 88;
    private static final int PANEL_PADDING = 16;
    private static final int PANEL_BORDER = 2;
    private static final int TITLE_AREA = 52;

    private static final int PANEL_BG = 0xF0161820; // More opaque, slate-dark
    private static final int PANEL_BORDER_COLOR = 0xFF5D4037; // Dark brown/bronze border
    private static final int PANEL_ACCENT = 0xFF8D6E63; // Lighter brown/bronze inner
    private static final int CORNER_COLOR = 0xFFFFD700; // Gold corners

    private final Screen parent;
    private MobListWidget listWidget;

    public LevelProgressScreen(Screen parent) {
        super(Text.translatable("screen.essencelib.level_progress"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int listWidth = COLS * CELL_SIZE;
        int panelWidth = listWidth + PANEL_PADDING * 2 + PANEL_BORDER * 2;
        panelWidth = Math.min(panelWidth, width - 48);
        listWidth = panelWidth - PANEL_PADDING * 2 - PANEL_BORDER * 2;
        int listHeight = height - TITLE_AREA - 50;
        int listX = (width - panelWidth) / 2 + PANEL_BORDER + PANEL_PADDING;
        int listY = TITLE_AREA + PANEL_BORDER + PANEL_PADDING;

        LevelComponent levelComp = client != null && client.player != null
            ? LevelComponent.getNullable(client.player)
            : null;

        List<EntityType<?>> allTypes = LevelManager.getKillableEntityTypes().toList();
        List<List<EntityType<?>>> rows = new ArrayList<>();
        for (int i = 0; i < allTypes.size(); i += COLS) {
            rows.add(allTypes.subList(i, Math.min(i + COLS, allTypes.size())));
        }

        listWidget = new MobListWidget(client, listWidth, listHeight, listY, ROW_HEIGHT, levelComp, rows);
        listWidget.setX(listX);
        listWidget.setY(listY);

        addDrawableChild(listWidget);

        int btnY = height - 32;
        addDrawableChild(net.minecraft.client.gui.widget.ButtonWidget.builder(
            Text.translatable("gui.done"),
            b -> close()
        ).dimensions(width / 2 - 100, btnY, 200, 20).build());
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dark overlay without blur - keeps text readable
        context.fill(0, 0, width, height, 0xE005050A); // Slightly bluer, darker backdrop
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        int panelWidth = COLS * CELL_SIZE + PANEL_PADDING * 2 + PANEL_BORDER * 2;
        panelWidth = Math.min(panelWidth, width - 48);
        int panelHeight = height - TITLE_AREA - 40;
        int panelX = (width - panelWidth) / 2;
        int panelY = TITLE_AREA;

        // --- RPG Panel Background ---
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, PANEL_BG);
        
        // Outer Border
        context.fill(panelX, panelY, panelX + panelWidth, panelY + PANEL_BORDER, PANEL_BORDER_COLOR);
        context.fill(panelX, panelY + panelHeight - PANEL_BORDER, panelX + panelWidth, panelY + panelHeight, PANEL_BORDER_COLOR);
        context.fill(panelX, panelY, panelX + PANEL_BORDER, panelY + panelHeight, PANEL_BORDER_COLOR);
        context.fill(panelX + panelWidth - PANEL_BORDER, panelY, panelX + panelWidth, panelY + panelHeight, PANEL_BORDER_COLOR);
        
        // Inner Bevel (Accent)
        context.fill(panelX + PANEL_BORDER, panelY + PANEL_BORDER, panelX + panelWidth - PANEL_BORDER, panelY + PANEL_BORDER + 1, PANEL_ACCENT);
        context.fill(panelX + PANEL_BORDER, panelY + PANEL_BORDER, panelX + PANEL_BORDER + 1, panelY + panelHeight - PANEL_BORDER, PANEL_ACCENT);
        
        // RPG Corner Blocks (Gold/Amber)
        int cornerSize = 4;
        context.fill(panelX - 1, panelY - 1, panelX + cornerSize, panelY + cornerSize, CORNER_COLOR); // TL
        context.fill(panelX + panelWidth - cornerSize, panelY - 1, panelX + panelWidth + 1, panelY + cornerSize, CORNER_COLOR); // TR
        context.fill(panelX - 1, panelY + panelHeight - cornerSize, panelX + cornerSize, panelY + panelHeight + 1, CORNER_COLOR); // BL
        context.fill(panelX + panelWidth - cornerSize, panelY + panelHeight - cornerSize, panelX + panelWidth + 1, panelY + panelHeight + 1, CORNER_COLOR); // BR
        
        // Dark inner cutouts for the corners
        context.fill(panelX, panelY, panelX + 2, panelY + 2, 0xFF000000);
        context.fill(panelX + panelWidth - 2, panelY, panelX + panelWidth, panelY + 2, 0xFF000000);
        context.fill(panelX, panelY + panelHeight - 2, panelX + 2, panelY + panelHeight, 0xFF000000);
        context.fill(panelX + panelWidth - 2, panelY + panelHeight - 2, panelX + panelWidth, panelY + panelHeight, 0xFF000000);

        // Header Divider Line (Aesthetic separator below the top frame)
        int dividerY = panelY - 14;
        context.fill(panelX + 10, dividerY, panelX + panelWidth - 10, dividerY + 1, 0x80FFFFFF); // Light faint line

        // Title
        Text titleText = title.copy().formatted(Formatting.GOLD, Formatting.BOLD);
        context.drawCenteredTextWithShadow(textRenderer, titleText, width / 2, 10, 0xFFFFFF);

        int level = client != null && client.player != null ? LevelManager.getLevel(client.player) : 0;
        int uniqueKills = client != null && client.player != null
            ? LevelManager.getCurrentUniqueKillsCount(client.player)
            : 0;
        int total = LevelManager.getTotalEntityCount();
        int requiredForNext = level >= LevelManager.MAX_LEVEL ? total : LevelManager.getRequiredKills(level + 1);
        int requiredForCurrent = level == 0 ? 0 : LevelManager.getRequiredKills(level);

        // --- LitRPG Level Stats ---
        int barWidth = 200;
        int barHeight = 8;
        int barX = width / 2 - barWidth / 2;
        int barY = 24;

        // Calculate progress within current level
        float progress = 0f;
        String killsText = "";
        if (level >= LevelManager.MAX_LEVEL) {
            progress = 1f;
            killsText = "MAX LEVEL";
        } else if (level == 0) {
            progress = requiredForNext > 0 ? (float) uniqueKills / requiredForNext : 0f;
            killsText = uniqueKills + " / " + requiredForNext + " EXP";
        } else {
            int neededForNext = requiredForNext - requiredForCurrent;
            int currentProgress = uniqueKills - requiredForCurrent;
            progress = neededForNext > 0 ? (float) currentProgress / neededForNext : 0f;
            progress = Math.min(1f, Math.max(0f, progress));
            killsText = currentProgress + " / " + neededForNext + " EXP";
        }

        // Draw LitRPG Progress Bar Frame
        context.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xFF403020); // Dark border
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF101015); // Empty track
        
        int fillW = (int)(barWidth * progress);
        if (fillW > 0) {
            // Gold / Amber XP Bar
            context.fill(barX, barY, barX + fillW, barY + barHeight, 0xFFFFB300); // Main fill
            context.fill(barX, barY, barX + fillW, barY + 2, 0x60FFFFFF); // Top shine
            context.fill(barX, barY + barHeight - 2, barX + fillW, barY + barHeight, 0x40000000); // Bottom shadow
            
            // Segment lines for blocky RPG look
            for (int pX = 20; pX < fillW; pX += 20) {
                context.fill(barX + pX, barY, barX + pX + 1, barY + barHeight, 0x40000000);
            }
        }
        
        // Frame corner dots
        context.fill(barX - 2, barY - 1, barX, barY + 1, 0xFFFFD700);
        context.fill(barX + barWidth, barY - 1, barX + barWidth + 2, barY + 1, 0xFFFFD700);
        context.fill(barX - 2, barY + barHeight - 1, barX, barY + barHeight + 1, 0xFFFFD700);
        context.fill(barX + barWidth, barY + barHeight - 1, barX + barWidth + 2, barY + barHeight + 1, 0xFFFFD700);

        // Draw text: "Lv.X" on the left, "Kills/Required" on the right
        Text levelText = Text.literal("Lv. " + level).formatted(Formatting.GOLD, Formatting.BOLD);
        Text killsDisplay = Text.literal(killsText).formatted(Formatting.GRAY);
        
        // Shadowed text around the bar
        context.drawTextWithShadow(textRenderer, levelText, barX - textRenderer.getWidth(levelText) - 6, barY, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, killsDisplay, barX + barWidth + 6, barY, 0xAAAAAA);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        MobRowEntry.clearEntityCache();
        if (client != null) {
            client.setScreen(parent);
        }
    }

    private static class MobListWidget extends ElementListWidget<MobRowEntry> {

        public MobListWidget(MinecraftClient client, int width, int height, int y, int itemHeight,
                            LevelComponent levelComp, List<List<EntityType<?>>> rows) {
            super(client, width, height, y, itemHeight);
            for (List<EntityType<?>> row : rows) {
                addEntry(new MobRowEntry(row, levelComp));
            }
        }
    }

    private static class MobRowEntry extends ElementListWidget.Entry<MobRowEntry> {

        private static final int ICON_SIZE = 52;
        private static final int KILLED_COLOR = 0xFF2E7D32; // Deeper emerald green for RPG vibe
        private static final int KILLED_ACCENT = 0xFF81C784; // Light green inner rim
        private static final int KILLED_BORDER = 0xFF1B5E20; // Dark green outer border
        
        /** Background for unkilled cells - dark gray so black silhouette is visible */
        private static final int SILHOUETTE_BG = 0xFF1E2024; // Very dark slate
        private static final int SILHOUETTE_BG_BORDER = 0xFF2A2D33; // Slightly lighter slate border
        private static final int SILHOUETTE_OUTER = 0xFF121417; // Darkest outer border

        private static final Map<EntityType<?>, LivingEntity> ENTITY_CACHE = new HashMap<>();

        /** Truncates name to fit cell width, appending "..." if needed. */
        private static String truncateName(TextRenderer tr, Text name, int maxWidth) {
            String s = name.getString();
            if (tr.getWidth(s) <= maxWidth) return s;
            int ellipsisW = tr.getWidth("...");
            for (int len = s.length(); len > 0; len--) {
                String sub = s.substring(0, len);
                if (tr.getWidth(sub) + ellipsisW <= maxWidth) return sub + "...";
            }
            return "...";
        }

        private final List<EntityType<?>> mobsInRow;
        private final LevelComponent levelComp;

        MobRowEntry(List<EntityType<?>> mobsInRow, LevelComponent levelComp) {
            this.mobsInRow = mobsInRow;
            this.levelComp = levelComp;
        }

        @Override
        public void render(DrawContext context, int index, int rowY, int rowX, int entryWidth, int entryHeight,
                          int mouseX, int mouseY, boolean hovered, float tickDelta) {
            MinecraftClient client = MinecraftClient.getInstance();
            World world = client.world;
            int cellWidth = entryWidth / COLS;

            for (int col = 0; col < mobsInRow.size(); col++) {
                EntityType<?> entityType = mobsInRow.get(col);
                Identifier id = Registries.ENTITY_TYPE.getId(entityType);
                boolean killed = levelComp != null && levelComp.hasUniqueKill(id);
                int killCount = levelComp != null ? levelComp.getKillCount(id) : 0;

                int cellX = rowX + col * cellWidth;
                int iconX = cellX + (cellWidth - ICON_SIZE) / 2;
                int iconY = rowY + 2;

                // Fixed slot center - avoids jitter when scrolling (mouse-based look caused movement)
                float lookX = iconX + ICON_SIZE / 2f;
                float lookY = iconY + ICON_SIZE / 2f;

                if (killed) {
                    LivingEntity living = getOrCreateEntity(entityType, world);
                    if (living != null) {
                        context.enableScissor(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE);
                        MobEntityRenderer.renderMobInGui(context, iconX, iconY, ICON_SIZE, living, false, lookX, lookY);
                        context.disableScissor();
                    } else {
                        SpawnEggItem spawnEgg = SpawnEggItem.forEntity(entityType);
                        if (spawnEgg != null) {
                            // Render egg with a fancy lit-RPG border behind it
                            context.fill(iconX - 2, iconY - 2, iconX + ICON_SIZE + 2, iconY + ICON_SIZE + 2, KILLED_BORDER);
                            context.fill(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE, KILLED_COLOR);
                            context.fill(iconX + 1, iconY + 1, iconX + ICON_SIZE - 1, iconY + ICON_SIZE - 1, KILLED_ACCENT);
                            // Draw item with a slight offset to center it in the slot
                            context.drawItem(new ItemStack(spawnEgg), iconX + (ICON_SIZE-16)/2, iconY + (ICON_SIZE-16)/2);
                        } else {
                            // Render a glowing empty slot or generic icon placeholder
                            context.fill(iconX - 2, iconY - 2, iconX + ICON_SIZE + 2, iconY + ICON_SIZE + 2, KILLED_BORDER);
                            context.fill(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE, KILLED_COLOR);
                            context.fill(iconX + 1, iconY + 1, iconX + ICON_SIZE - 1, iconY + ICON_SIZE - 1, KILLED_ACCENT);
                        }
                    }
                    Text name = entityType.getName();
                    String nameStr = truncateName(client.textRenderer, name, cellWidth - 4);
                    String countStr = "×" + killCount;
                    int textY = rowY + ICON_SIZE + 4;
                    int textCenterX = cellX + cellWidth / 2;
                    int nameW = client.textRenderer.getWidth(nameStr);
                    int countW = client.textRenderer.getWidth(countStr);
                    // Render Nameplate Background (RPG Style)
                    context.fill(cellX + 2, textY - 2, cellX + cellWidth - 2, textY + 22, 0xFF21252B); // Solid inner
                    context.fill(cellX + 2, textY - 3, cellX + cellWidth - 2, textY - 2, 0xFF353B45); // Top highlight
                    context.fill(cellX + 2, textY + 22, cellX + cellWidth - 2, textY + 23, 0xFF121417); // Bottom shadow
                    context.fill(cellX + 1, textY - 2, cellX + 2, textY + 22, 0xFF353B45); // Left highlight
                    context.fill(cellX + cellWidth - 2, textY - 2, cellX + cellWidth - 1, textY + 22, 0xFF121417); // Right shadow
                    
                    context.enableScissor(cellX + 2, textY - 2, cellX + cellWidth - 2, textY + 22);
                    context.drawTextWithShadow(client.textRenderer, nameStr, textCenterX - nameW / 2, textY, 0xFFE0B0); // Slightly warm tint for killed
                    context.drawTextWithShadow(client.textRenderer, countStr, textCenterX - countW / 2, textY + 11, 0xA0A0A0); // Gray for count
                    context.disableScissor();
                } else {
                    // Draw non-black background so black silhouette is visible
                    context.fill(iconX - 2, iconY - 2, iconX + ICON_SIZE + 2, iconY + ICON_SIZE + 2, SILHOUETTE_OUTER);
                    context.fill(iconX - 1, iconY - 1, iconX + ICON_SIZE + 1, iconY + ICON_SIZE + 1, SILHOUETTE_BG_BORDER);
                    context.fill(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE, SILHOUETTE_BG);

                    LivingEntity living = getOrCreateEntity(entityType, world);
                    if (living != null) {
                        context.enableScissor(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE);
                        MobEntityRenderer.renderMobInGui(context, iconX, iconY, ICON_SIZE, living, true, lookX, lookY);
                        context.disableScissor();
                    } else {
                        int qW = client.textRenderer.getWidth("?");
                        context.drawText(client.textRenderer, "?", iconX + (ICON_SIZE - qW) / 2, iconY + (ICON_SIZE - client.textRenderer.fontHeight) / 2, 0x888888, false);
                    }
                    // Show mob name for unkilled slots so each silhouette is identifiable
                    Text name = entityType.getName();
                    String nameStr = truncateName(client.textRenderer, name, cellWidth - 4);
                    int textY = rowY + ICON_SIZE + 4;
                    int textCenterX = cellX + cellWidth / 2;
                    int nameW = client.textRenderer.getWidth(nameStr);
                    // Render Nameplate Background for Locked
                    context.fill(cellX + 2, textY - 2, cellX + cellWidth - 2, textY + 11, 0xFF181A1F); // Darker inner
                    context.fill(cellX + 2, textY - 3, cellX + cellWidth - 2, textY - 2, 0xFF282C34); // Top highlight
                    context.fill(cellX + 2, textY + 11, cellX + cellWidth - 2, textY + 12, 0xFF0D0F12); // Bottom shadow
                    context.fill(cellX + 1, textY - 2, cellX + 2, textY + 11, 0xFF282C34); // Left highlight
                    context.fill(cellX + cellWidth - 2, textY - 2, cellX + cellWidth - 1, textY + 11, 0xFF0D0F12); // Right shadow
                    
                    context.enableScissor(cellX + 2, textY - 2, cellX + cellWidth - 2, textY + 11);
                    context.drawTextWithShadow(client.textRenderer, nameStr, textCenterX - nameW / 2, textY, 0x777777); // Dim gray
                    context.disableScissor();
                }
            }
        }

        private static LivingEntity getOrCreateEntity(EntityType<?> type, World world) {
            if (world == null) return null;
            return ENTITY_CACHE.computeIfAbsent(type, t -> MobEntityRenderer.createEntityForRendering(t, world));
        }

        static void clearEntityCache() {
            ENTITY_CACHE.clear();
        }

        @Override
        public List<? extends net.minecraft.client.gui.Element> children() {
            return List.of();
        }

        @Override
        public List<? extends net.minecraft.client.gui.Selectable> selectableChildren() {
            return List.of();
        }
    }
}
