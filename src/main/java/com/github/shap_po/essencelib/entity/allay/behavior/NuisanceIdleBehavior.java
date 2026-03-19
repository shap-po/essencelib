package com.github.shap_po.essencelib.entity.allay.behavior;

import com.github.shap_po.essencelib.entity.NuisanceAllayHelper;
import com.github.shap_po.essencelib.entity.allay.NuisanceAllayMemories;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class NuisanceIdleBehavior extends ExtendedBehaviour<AllayEntity> {
    public NuisanceIdleBehavior() {
        this.startCondition(allay ->
            NuisanceAllayHelper.isNuisance(allay)
                && allay.getMainHandStack().isEmpty()
                && !BrainUtils.hasMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER)
        );
        this.whenStarting(allay -> {
            // #region agent log
            NuisanceAllayHelper.debugLog(
                allay,
                "H6",
                "NuisanceIdleBehavior#start",
                "Idle behavior started",
                "{\"entityId\":" + allay.getId() + ",\"hasFocus\":" + BrainUtils.hasMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER) + "}"
            );
            // #endregion
        });
    }

    @Override
    protected boolean shouldKeepRunning(AllayEntity allay) {
        return true;
    }

    @Override
    protected void tick(AllayEntity allay) {
        if (!(allay.getWorld() instanceof ServerWorld world)) return;
        if (!NuisanceAllayHelper.isNuisance(allay)) return;
        if (!allay.getMainHandStack().isEmpty()) return;

        if (BrainUtils.hasMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER)) return;

        NuisanceAllayHelper.runIdleDriftBehavior(allay, world);
    }

    @Override
    protected List<Pair<net.minecraft.entity.ai.brain.MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return List.of();
    }
}
