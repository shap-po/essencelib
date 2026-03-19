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

public class NuisanceStealBehavior extends ExtendedBehaviour<AllayEntity> {
    public NuisanceStealBehavior() {
        this.startCondition(allay -> {
            if (!NuisanceAllayHelper.isNuisance(allay)) return false;
            if (!allay.getMainHandStack().isEmpty()) return false;
            if (BrainUtils.hasMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER)) return true;
            if (!(allay.getWorld() instanceof ServerWorld world)) return false;
            return NuisanceAllayHelper.selectFocusPlayer(allay, world) != null;
        });
        this.whenStarting(allay -> {
            // #region agent log
            NuisanceAllayHelper.debugLog(
                allay,
                "H6",
                "NuisanceStealBehavior#start",
                "Steal behavior started",
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

        PlayerEntity focus = BrainUtils.getMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER);
        if (focus == null) {
            focus = NuisanceAllayHelper.selectFocusPlayer(allay, world);
            // #region agent log
            NuisanceAllayHelper.debugLog(
                allay,
                "H4",
                "NuisanceStealBehavior#tick",
                "Steal behavior missing focus memory; fallback selection attempted",
                "{\"entityId\":" + allay.getId() + ",\"focusFound\":" + (focus != null) + "}"
            );
            // #endregion
        }

        if ((allay.age + allay.getId()) % 20 == 0) {
            // #region agent log
            NuisanceAllayHelper.debugLog(
                allay,
                "H4",
                "NuisanceStealBehavior#tick",
                "Steal behavior tick running",
                "{\"entityId\":" + allay.getId() + ",\"focusPresent\":" + (focus != null) + ",\"holdingItem\":" + (!allay.getMainHandStack().isEmpty()) + "}"
            );
            // #endregion
        }
        NuisanceAllayHelper.runStealBehavior(allay, world, focus, world.getTime());
    }

    @Override
    protected List<Pair<net.minecraft.entity.ai.brain.MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return List.of();
    }
}
