package com.github.shap_po.essencelib.entity.allay;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;

public final class NuisanceAllayMemories {
    public static final MemoryModuleType<PlayerEntity> FOCUS_PLAYER = MemoryModuleType.NEAREST_VISIBLE_PLAYER;
    public static final MemoryModuleType<LivingEntity> CHASE_TARGET = MemoryModuleType.ATTACK_TARGET;
    public static final MemoryModuleType<ItemEntity> STEAL_TARGET = MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM;
    public static final MemoryModuleType<Boolean> HAS_LOS_THREAT = SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get();
    public static final MemoryModuleType<Boolean> HAS_CHEST_OPPORTUNITY = SBLMemoryTypes.TARGET_UNREACHABLE.get();

    private NuisanceAllayMemories() {
    }
}
