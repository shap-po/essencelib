package com.github.shap_po.essencelib.entity.allay.sensor;

import com.github.shap_po.essencelib.entity.NuisanceAllayHelper;
import com.github.shap_po.essencelib.entity.allay.NuisanceAllayMemories;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

public class NuisanceLineOfSightSensor extends NearbyPlayersSensor<AllayEntity> {
    @Override
    protected void sense(ServerWorld world, AllayEntity allay) {
        super.sense(world, allay);

        PlayerEntity focus = BrainUtils.getMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER);
        boolean seen = focus != null && NuisanceAllayHelper.isSeenByPlayer(focus, allay);
        boolean chestOpportunity = NuisanceAllayHelper.hasNearbyStealableChestWindow(allay, world);

        if (seen) {
            BrainUtils.setMemory(allay, NuisanceAllayMemories.HAS_LOS_THREAT, true);
        } else {
            BrainUtils.clearMemory(allay, NuisanceAllayMemories.HAS_LOS_THREAT);
        }

        if (chestOpportunity) {
            BrainUtils.setMemory(allay, NuisanceAllayMemories.HAS_CHEST_OPPORTUNITY, true);
        } else {
            BrainUtils.clearMemory(allay, NuisanceAllayMemories.HAS_CHEST_OPPORTUNITY);
        }
    }
}
