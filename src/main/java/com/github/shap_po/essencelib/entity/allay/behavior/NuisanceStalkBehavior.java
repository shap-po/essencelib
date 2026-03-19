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

public class NuisanceStalkBehavior extends ExtendedBehaviour<AllayEntity> {
    public NuisanceStalkBehavior() {
        this.startCondition(allay -> {
            if (!(allay.getWorld() instanceof ServerWorld world)) return false;
            if (!NuisanceAllayHelper.isNuisance(allay)) return false;
            if (!allay.getMainHandStack().isEmpty()) return false;
            PlayerEntity focus = BrainUtils.getMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER);
            if (focus == null) return false;
            if (NuisanceAllayHelper.isPlayerChasing(allay, focus, world.getTime())) return false;
            return !NuisanceAllayHelper.isSeenByPlayer(focus, allay);
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
        }
        if (focus == null) return;

        if (NuisanceAllayHelper.isPlayerChasing(allay, focus, world.getTime())) return;
        if (NuisanceAllayHelper.isSeenByPlayer(focus, allay)) return;

        if (!NuisanceAllayHelper.runStalkWithCoverBehavior(allay, focus, world, world.getTime())) {
            NuisanceAllayHelper.runStalkPatrolBehavior(allay, focus, world);
        }
    }

    @Override
    protected List<Pair<net.minecraft.entity.ai.brain.MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return List.of();
    }
}
