package com.github.shap_po.essencelib.entity.allay.sensor;

import com.github.shap_po.essencelib.entity.NuisanceAllayHelper;
import com.github.shap_po.essencelib.entity.allay.NuisanceAllayMemories;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

public class NuisanceFocusPlayerSensor extends NearbyPlayersSensor<AllayEntity> {
    public NuisanceFocusPlayerSensor() {
        this.setScanRate(allay -> 4);
    }

    @Override
    protected void sense(ServerWorld world, AllayEntity allay) {
        super.sense(world, allay);

        if (!NuisanceAllayHelper.isNuisance(allay)) {
            BrainUtils.clearMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER);
            return;
        }

        PlayerEntity focus = NuisanceAllayHelper.selectFocusPlayer(allay, world);
        if (focus == null) {
            // #region agent log
            NuisanceAllayHelper.debugLog(
                allay,
                "H3",
                "NuisanceFocusPlayerSensor#sense",
                "No focus player found",
                "{\"entityId\":" + allay.getId() + "}"
            );
            // #endregion
            BrainUtils.clearMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER);
            return;
        }

        BrainUtils.setMemory(allay, NuisanceAllayMemories.FOCUS_PLAYER, focus);
        if ((allay.age + allay.getId()) % 20 == 0) {
            // #region agent log
            NuisanceAllayHelper.debugLog(
                allay,
                "H3",
                "NuisanceFocusPlayerSensor#sense",
                "Focus player selected",
                "{\"entityId\":" + allay.getId() + ",\"focusPlayerId\":" + focus.getId() + ",\"focusDistSq\":" + allay.squaredDistanceTo(focus) + "}"
            );
            // #endregion
        }
    }
}
