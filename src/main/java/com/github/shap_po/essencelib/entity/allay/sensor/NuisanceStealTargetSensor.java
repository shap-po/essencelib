package com.github.shap_po.essencelib.entity.allay.sensor;

import com.github.shap_po.essencelib.entity.NuisanceAllayHelper;
import com.github.shap_po.essencelib.entity.allay.NuisanceAllayMemories;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyItemsSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

public class NuisanceStealTargetSensor extends NearbyItemsSensor<AllayEntity> {
    @Override
    protected void doTick(ServerWorld world, AllayEntity allay) {
        super.doTick(world, allay);

        if (!NuisanceAllayHelper.isNuisance(allay)) {
            BrainUtils.clearMemory(allay, NuisanceAllayMemories.STEAL_TARGET);
            return;
        }

        PlayerEntity focus = BrainUtils.getMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER);
        ItemEntity target = NuisanceAllayHelper.findBestGroundItemTarget(allay, world, focus);
        if (target == null || !target.isAlive()) {
            BrainUtils.clearMemory(allay, NuisanceAllayMemories.STEAL_TARGET);
            return;
        }

        BrainUtils.setMemory(allay, NuisanceAllayMemories.STEAL_TARGET, target);
    }
}
