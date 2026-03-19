package com.github.shap_po.essencelib.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Reusable combat analysis component for "aggro awareness" style abilities.
 */
public final class AggroAnalysisHelper {

    public static boolean isMobAggroingPlayer(MobEntity mob, PlayerEntity player) {
        if (mob == null || player == null || !mob.isAlive() || mob.isRemoved()) return false;
        LivingEntity target = mob.getTarget();
        return target == player;
    }

    public static Analysis analyze(PlayerEntity player, MobEntity mob, double moveX, double moveZ, double minMoveSq, double facingTargetDotMin) {
        double playerX = player.getX();
        double playerZ = player.getZ();
        double moveSq = moveX * moveX + moveZ * moveZ;

        double toMobX = mob.getX() - playerX;
        double toMobZ = mob.getZ() - playerZ;
        boolean movingAway = moveSq > minMoveSq && (moveX * toMobX + moveZ * toMobZ) < 0.0;

        var look = player.getRotationVec(1.0F);
        double toMobLen = Math.sqrt(toMobX * toMobX + toMobZ * toMobZ);
        double toMobDirX = toMobLen > 1.0E-6 ? toMobX / toMobLen : 0.0;
        double toMobDirZ = toMobLen > 1.0E-6 ? toMobZ / toMobLen : 0.0;
        double lookDotTarget = look.x * toMobDirX + look.z * toMobDirZ;
        boolean facingTarget = lookDotTarget > facingTargetDotMin;
        boolean facingAway = lookDotTarget < 0.0;

        return new Analysis(movingAway, facingTarget, facingAway, lookDotTarget);
    }

    public static boolean hasRetreatIntent(boolean movingAway, boolean sprinting, boolean facingTarget) {
        return movingAway || (sprinting && !facingTarget);
    }

    public record Analysis(
        boolean movingAway,
        boolean facingTarget,
        boolean facingAway,
        double lookDotTarget
    ) {}

    private AggroAnalysisHelper() {}
}
