package com.github.shap_po.essencelib.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public final class DownedStateHelper {

    private static final Text DOWNED_MESSAGE = Text.literal("You are downed. Wait till revived or brought back to life.");

    public static void sendDownedActionbar(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(DOWNED_MESSAGE, true);
        }
    }

    /**
     * Clears nearby hostile focus on the downed player so mobs disengage reliably.
     */
    public static void clearMobAggroAround(PlayerEntity player, double radius) {
        World world = player.getWorld();
        if (world.isClient) {
            return;
        }
        Box box = player.getBoundingBox().expand(radius);
        for (MobEntity mob : world.getEntitiesByClass(MobEntity.class, box, m -> m.getTarget() == player)) {
            mob.setTarget(null);
        }
    }

    private DownedStateHelper() {}
}
