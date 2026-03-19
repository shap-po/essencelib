package com.github.shap_po.essencelib.entity.allay.sensor;

import com.github.shap_po.essencelib.entity.NuisanceAllayHelper;
import com.github.shap_po.essencelib.entity.allay.NuisanceAllayMemories;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

public class NuisanceChaseSensor extends NearbyPlayersSensor<AllayEntity> {
    @Override
    protected void sense(ServerWorld world, AllayEntity allay) {
        super.sense(world, allay);

        PlayerEntity focus = BrainUtils.getMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER);
        if (focus == null || !focus.isAlive()) {
            BrainUtils.clearMemory(allay, NuisanceAllayMemories.CHASE_TARGET);
            return;
        }

        if (NuisanceAllayHelper.isPlayerChasing(allay, focus, world.getTime())) {
            BrainUtils.setMemory(allay, NuisanceAllayMemories.CHASE_TARGET, focus);
        } else {
            BrainUtils.clearMemory(allay, NuisanceAllayMemories.CHASE_TARGET);
        }
    }
}
