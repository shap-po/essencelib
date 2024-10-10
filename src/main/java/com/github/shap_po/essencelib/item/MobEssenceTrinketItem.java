package com.github.shap_po.essencelib.item;

import com.github.shap_po.essencelib.component.item.MobEssenceItemComponent;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.util.Rarity;

public class MobEssenceTrinketItem extends TrinketItem {
    public MobEssenceTrinketItem() {
        super(
            new Settings()
                .maxCount(1)
                .rarity(Rarity.RARE)
                .component(ModDataComponentTypes.MOB_ESSENCE, MobEssenceItemComponent.DEFAULT)
        );
    }
}
