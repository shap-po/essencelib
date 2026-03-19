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

public class NuisanceHoldingBehavior extends ExtendedBehaviour<AllayEntity> {
    public NuisanceHoldingBehavior() {
        this.startCondition(allay ->
            NuisanceAllayHelper.isNuisance(allay) && !allay.getMainHandStack().isEmpty()
        );
        this.whenStarting(allay -> {
            // #region agent log
            NuisanceAllayHelper.debugLog(
                allay,
                "H6",
                "NuisanceHoldingBehavior#start",
                "Holding behavior started",
                "{\"entityId\":" + allay.getId() + ",\"holding\":" + (!allay.getMainHandStack().isEmpty()) + "}"
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
        if (allay.getMainHandStack().isEmpty()) return;

        PlayerEntity focus = BrainUtils.getMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER);
        if (focus == null) {
            focus = NuisanceAllayHelper.selectFocusPlayer(allay, world);
        }

        NuisanceAllayHelper.runHoldingBehavior(allay, world, focus, world.getTime());
    }

    @Override
    protected List<Pair<net.minecraft.entity.ai.brain.MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return List.of();
    }
}
