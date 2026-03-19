package com.github.shap_po.essencelib.guillotine;

import com.github.shap_po.essencelib.component.RestrainedComponent;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Helper for the guillotine (or any restraint block). Use this from your block entity to
 * put a player in the guillotine so they cannot shift out or move until released or executed.
 */
public final class RestrainedHelper {
    // Fixed guillotine orientation uses a prone-style execution pose aimed through the front slot.
    private static final double POSE_OFFSET_X = 0.5;
    private static final double POSE_OFFSET_Y = 0.34;
    private static final double POSE_OFFSET_Z = -0.22;
    private static final double SNAP_DISTANCE_SQ = 0.01;

    private RestrainedHelper() {}

    /**
     * Restrain a player at the given block position. They will be held in place and cannot
     * move or sneak out. Call from your guillotine block entity when the player is in the
     * execution zone.
     *
     * @param player the player to restrain
     * @param anchorBlockPos the block pos they are standing on (e.g. the guillotine base block)
     * @param dimension the world they are in
     */
    public static void restrain(@Nullable PlayerEntity player, BlockPos anchorBlockPos, RegistryKey<World> dimension) {
        if (player == null) return;
        RestrainedComponent comp = RestrainedComponent.getNullable(player);
        if (comp != null) {
            comp.restrain(anchorBlockPos.toImmutable(), dimension);
        }
    }

    /**
     * Release a player so they can move again. Call when the guillotine releases them
     * (e.g. redstone off) or before execution if you kill them (so they are not restrained when dead).
     */
    public static void release(@Nullable PlayerEntity player) {
        if (player == null) return;
        RestrainedComponent comp = RestrainedComponent.getNullable(player);
        if (comp != null) {
            comp.release();
        }
    }

    /**
     * Returns true if the player is currently restrained (e.g. in a guillotine).
     */
    public static boolean isRestrained(@Nullable PlayerEntity player) {
        return RestrainedComponent.isRestrained(player);
    }

    /**
     * Force player into a prone-style guillotine pose while allowing camera look.
     * Called both on command placement and every server tick while restrained.
     */
    public static void snapToGuillotinePose(@Nullable PlayerEntity player, BlockPos anchorBlockPos) {
        if (player == null) return;
        Vec3d target = getGuillotinePosePosition(anchorBlockPos);
        boolean needsSnap = player.squaredDistanceTo(target.x, target.y, target.z) > SNAP_DISTANCE_SQ;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            // Teleport only when drifted to reduce camera jitter while still enforcing lock.
            if (needsSnap) {
                serverPlayer.networkHandler.requestTeleport(target.x, target.y, target.z, player.getYaw(), player.getPitch());
            }
        } else {
            if (needsSnap) {
                player.refreshPositionAndAngles(target.x, target.y, target.z, player.getYaw(), player.getPitch());
            }
        }
        player.setVelocity(Vec3d.ZERO);
        player.velocityModified = true;
        player.setOnGround(true);
        player.setSprinting(false);
        player.setSwimming(true);
        player.setSneaking(false);
        player.setPose(EntityPose.SWIMMING);
    }

    public static Vec3d getGuillotinePosePosition(BlockPos anchorBlockPos) {
        return new Vec3d(
            anchorBlockPos.getX() + POSE_OFFSET_X,
            anchorBlockPos.getY() + POSE_OFFSET_Y,
            anchorBlockPos.getZ() + POSE_OFFSET_Z
        );
    }
}
