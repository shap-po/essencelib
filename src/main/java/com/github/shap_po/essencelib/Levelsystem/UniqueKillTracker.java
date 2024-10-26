
package com.github.shap_po.essencelib.Levelsystem;

import net.minecraft.entity.EntityType;

import java.util.HashSet;
import java.util.Set;

public class UniqueKillTracker {
    private final Set<EntityType<?>> uniqueKills = new HashSet<>();
    private int xp = 0;

    public void addKill(EntityType<?> entityType) {
        if (uniqueKills.add(entityType)) {
            xp++;
        }
    }

    public int getXp() {
        return xp;
    }

    public int getLevel(int maxLevel) {
        int requiredXp = 0;
        for (int level = 1; level <= maxLevel; level++) {
            requiredXp += level;
            if (xp < requiredXp) {
                return level - 1;
            }
        }
        return maxLevel;
    }
}