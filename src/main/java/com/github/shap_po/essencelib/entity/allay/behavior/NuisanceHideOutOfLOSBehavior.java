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

public class NuisanceHideOutOfLOSBehavior extends ExtendedBehaviour<AllayEntity> {
    public NuisanceHideOutOfLOSBehavior() {
        this.startCondition(allay -> {
            if (!NuisanceAllayHelper.isNuisance(allay)) return false;
            if (!allay.getMainHandStack().isEmpty()) return false;
            PlayerEntity focus = BrainUtils.getMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER);
            return focus != null && NuisanceAllayHelper.isSeenByPlayer(focus, allay);
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
        if (focus == null) return;

        if (NuisanceAllayHelper.isSeenByPlayer(focus, allay)) {
            NuisanceAllayHelper.runHideOutOfSightBehavior(allay, focus, world, world.getTime());
        }
    }

    @Override
    protected List<Pair<net.minecraft.entity.ai.brain.MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return List.of();
    }
}
