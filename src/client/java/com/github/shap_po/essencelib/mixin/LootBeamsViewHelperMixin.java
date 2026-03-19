package com.github.shap_po.essencelib.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * Essence items get Loot Beams via custom_rarities.json (essences). LootBeamsNameTagRendererMixin
 * disables the Loot Beams nametag; we use EssenceFloatingTooltipRenderer instead.
 */
@Mixin(value = com.lootbeams.helpers.ViewHelper.class, remap = false)
public abstract class LootBeamsViewHelperMixin {
}
