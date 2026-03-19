package com.github.shap_po.essencelib.entity.allay.behavior;

import com.github.shap_po.essencelib.entity.NuisanceAllayHelper;
import com.github.shap_po.essencelib.entity.allay.NuisanceAllayMemories;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class NuisanceAcquireFocusBehavior extends ExtendedBehaviour<AllayEntity> {
    public NuisanceAcquireFocusBehavior() {
        this.startCondition(allay ->
            NuisanceAllayHelper.isNuisance(allay) && !BrainUtils.hasMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER)
        );
        this.whenStarting(allay -> {
            // #region agent log
            NuisanceAllayHelper.debugLog(
                allay,
                "H6",
                "NuisanceAcquireFocusBehavior#start",
                "AcquireFocus behavior started",
                "{\"entityId\":" + allay.getId() + "}"
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
        NuisanceAllayHelper.setDebugState(allay, "acquire");

        PlayerEntity focus = NuisanceAllayHelper.selectFocusPlayer(allay, world);
        if (focus != null) {
            BrainUtils.setMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER, focus);
            // #region agent log
            NuisanceAllayHelper.debugLog(
                allay,
                "H5",
                "NuisanceAcquireFocusBehavior#tick",
                "AcquireFocus behavior set focus",
                "{\"entityId\":" + allay.getId() + ",\"focusPlayerId\":" + focus.getId() + "}"
            );
            // #endregion
            return;
        }

        // No candidate yet: stay active with search-like drifting instead of default-like idle.
        // #region agent log
        NuisanceAllayHelper.debugLog(
            allay,
            "H5",
            "NuisanceAcquireFocusBehavior#tick",
            "AcquireFocus behavior had no candidate",
            "{\"entityId\":" + allay.getId() + "}"
        );
        // #endregion
        NuisanceAllayHelper.runIdleDriftBehavior(allay, world);
    }

    @Override
    protected List<Pair<net.minecraft.entity.ai.brain.MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return List.of();
    }
}
