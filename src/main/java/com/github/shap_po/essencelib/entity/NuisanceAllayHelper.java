package com.github.shap_po.essencelib.entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.registry.ModSounds;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public final class NuisanceAllayHelper {
    public static final String NUISANCE_TAG = "essencelib_nuisance_allay";
    public static final String NUISANCE_TAMED_TAG = "essencelib_nuisance_tamed";

    private static final String STOLEN_AT_TAG_PREFIX = "essencelib_nuisance_stolen_at_";
    private static final String PANIC_UNTIL_TAG_PREFIX = "essencelib_nuisance_panic_until_";
    private static final String REST_UNTIL_TAG_PREFIX = "essencelib_nuisance_rest_until_";
    private static final String GLOAT_UNTIL_TAG_PREFIX = "essencelib_nuisance_gloat_until_";
    private static final String ORBIT_UNTIL_TAG_PREFIX = "essencelib_nuisance_orbit_until_";
    private static final String CHASE_UNTIL_TAG_PREFIX = "essencelib_nuisance_chase_until_";
    private static final String CLIMB_UNTIL_TAG_PREFIX = "essencelib_nuisance_climb_until_";
    private static final String STOLEN_PLAYER_TAG_PREFIX = "essencelib_nuisance_stolen_player_";
    private static final String GLOAT_LAUGH_PLAYED_TAG_PREFIX = "essencelib_nuisance_gloat_laugh_played_";
    private static final String LAST_GLOAT_END_TAG_PREFIX = "essencelib_nuisance_last_gloat_end_";
    private static final String CHEST_WAIT_GLOAT_TAG = "essencelib_nuisance_chest_wait_gloat";
    private static final String PLAYER_LOOT_STEAL_TAG = "essencelib_nuisance_player_loot_steal";
    private static final String BOREDOM_TAG_PREFIX = "essencelib_nuisance_boredom_";
    private static final String DEBUG_STATE_TAG_PREFIX = "essencelib_nuisance_debug_state_";
    private static final boolean DEBUG_RUNTIME_LOGGING_ENABLED = true;
    private static final String DEBUG_SESSION_ID = "041368";
    private static final String DEBUG_RUN_ID = "run1";
    private static final Path DEBUG_LOG_PATH = Path.of("d:\\codding\\debug-041368.log");

    private static final float ITEM_DESPAWN_SPAWN_CHANCE = 0.10f;
    private static final int NATURAL_ITEM_DESPAWN_AGE = 6000;
    private static final int PLAYER_KILL_LOOT_AGE_TICKS = 70;
    private static final int MAX_BOREDOM_TICKS = 12000; // 10 minutes

    private static final double SPAWN_PLAYER_NEARBY_RANGE = 80.0;
    private static final double SPAWN_EXCLUSION_RANGE = 96.0; // 6 chunks
    private static final double PLAYER_INTEREST_RANGE = 192.0;
    private static final double STEAL_ITEM_RANGE = 12.0;
    private static final int CHEST_SCAN_RANGE = 16;
    private static final double PLAYER_LOOT_FOCUS_RANGE = 8.0;
    private static final double IMMEDIATE_GRAB_RANGE = 3.4;
    private static final double CLOSE_COMMIT_STEAL_RANGE = 6.5;
    private static final double CLOSE_COMMIT_INSTANT_GRAB_RANGE = 5.8;

    private static final double HEIGHT_BAND_MIN_ABOVE_PLAYER = 1.5;
    private static final double HEIGHT_BAND_MAX_ABOVE_PLAYER = 5.0;
    private static final double GLOAT_HEIGHT_MIN_ABOVE_PLAYER = 2.0;
    private static final double GLOAT_HEIGHT_MAX_ABOVE_PLAYER = 3.5;

    // Sight and stalking thresholds.
    private static final double STALK_SEEN_DOT = 0.60;
    private static final double STALK_TURNING_TO_SEE_DOT = 0.48;
    private static final double DIRECT_STARE_DOT = 0.86;
    private static final float PRETURN_YAW_THRESHOLD_DEG = 3.5f;
    private static final double PRETURN_FRONT_HEMISPHERE_DOT = 0.08;

    // Distance bands with hysteresis-like separation to avoid jitter.
    private static final double FLEE_REENGAGE_DISTANCE = 4.8;
    private static final double FLEE_HARD_DISTANCE = 3.0;
    private static final double EMERGENCY_HIDE_DISTANCE = 4.2;
    private static final double COMFORT_DISTANCE = 7.0;
    private static final double STALK_MIN_DISTANCE = 3.6;
    private static final double STALK_MAX_DISTANCE = 8.2;
    private static final double HIDE_MIN_DISTANCE = 5.4;
    private static final double HIDE_PREFERRED_DISTANCE = 6.8;
    private static final double STALK_PREDICTION_SECONDS = 0.35;
    private static final double STALK_SIDE_AMPLITUDE = 2.3;
    private static final double COVER_STALK_MIN_DISTANCE = 3.2;
    private static final double COVER_STALK_MAX_DISTANCE = 9.5;
    private static final double MAX_HIDE_RING_DISTANCE = 13.5;
    private static final double TAKING_TURNS_PLAYER_PENALTY = 196.0;

    private static final long STEAL_CHECK_TICKS = 3L;
    private static final long PATH_UPDATE_TICKS = 3L;
    private static final long PANIC_SPRINT_TICKS = 28L;
    private static final long CLIMB_TICKS = 40L;
    private static final long CHASE_MEMORY_TICKS = 50L;
    private static final long GLOAT_TICKS = 20L * 2L;
    private static final long GLOAT_FATIGUE_TICKS = 20L * 5L;
    private static final long RECENT_STEAL_TAUNT_WINDOW_TICKS = 20L * 8L;
    private static final long TAUNT_ORBIT_TICKS = 20L * 3L;
    private static final long POST_GLOAT_REPOSITION_TICKS = 40L;
    private static final long POST_STEAL_INITIAL_GLOAT_TICKS = 12L;
    private static final long POST_STEAL_GETAWAY_TICKS = 20L;
    private static final long REST_TICKS = 20L * 7L;
    private static final long STOLEN_ITEM_TIMEOUT_TICKS = 1200L;
    private static final long PLAYER_LOOT_PRE_GLOAT_GETAWAY_TICKS = 14L;

    private static final double SPEED_STEAL_PURSUIT = 2.55;
    private static final double SPEED_SNEAK = 1.12;
    private static final double SPEED_HIDE = 1.20;
    private static final double SPEED_FLEE = 1.36;
    private static final double SPEED_PANIC = 1.42;
    private static final double SPEED_GLOAT_BOB = 1.0;
    private static final double SPEED_REST = 0.92;

    private static final double GLOAT_BOB_AMPLITUDE = 0.20;
    private static final double GLOAT_BOB_FREQUENCY = 1.40;
    private static final double GLOAT_LOOK_BOB_AMPLITUDE = 0.45;
    private static final double GLOAT_LOOK_BOB_FREQUENCY = 3.80;
    private static final long GLOAT_LAUGH_SOUND_INTERVAL_TICKS = 6L;
    private static final long DEBUG_HEAD_EFFECT_INTERVAL_TICKS = 6L;
    private static final boolean DEBUG_HEAD_EFFECT_ENABLED = true;
    private static final double TAUNT_TEASE_MIN_DISTANCE = 4.0;
    private static final double TAUNT_TEASE_MAX_DISTANCE = 11.0;
    private static final double TAUNT_REENGAGE_DISTANCE = 13.0;
    private static final double TAUNT_ORBIT_RADIUS = 2.2;
    private static final double RE_GLOAT_MIN_DISTANCE = 5.8;
    private static final double VALUE_PRIORITY_WEIGHT = 3.4;
    private static final double CHEST_VALUE_PRIORITY_WEIGHT = 6.0;

    private static final long MOVE_REPATH_MIN_INTERVAL_TICKS = 4L;
    private static final double MOVE_REPATH_MIN_DELTA_SQ = 0.16;

    private static final long CHEST_STEAL_WINDOW_TICKS = 20L * 8L;
    private static final long CHEST_STEAL_DELAY_MIN_TICKS = 2L;
    private static final long CHEST_STEAL_DELAY_MAX_TICKS = 8L;

    private static final Map<ChestWindowKey, ChestOpenWindow> OPENED_CHEST_WINDOWS = new HashMap<>();

    private NuisanceAllayHelper() {}

    public static void maybeSpawnFromItemDespawn(ItemEntity itemEntity) {
        if (!(itemEntity.getWorld() instanceof ServerWorld world)) return;
        if (itemEntity.getStack().isEmpty()) return;
        if (itemEntity.getItemAge() < NATURAL_ITEM_DESPAWN_AGE - 2) return;
        if (world.random.nextFloat() > ITEM_DESPAWN_SPAWN_CHANCE) return;

        PlayerEntity nearest = world.getClosestPlayer(itemEntity, SPAWN_PLAYER_NEARBY_RANGE);
        if (nearest == null) return;

        List<AllayEntity> existingAllays = world.getEntitiesByClass(
            AllayEntity.class, 
            itemEntity.getBoundingBox().expand(SPAWN_EXCLUSION_RANGE), 
            NuisanceAllayHelper::isNuisance
        );
        if (!existingAllays.isEmpty()) return;

        AllayEntity allay = EntityType.ALLAY.create(world);
        if (allay == null) return;
        allay.refreshPositionAndAngles(itemEntity.getX(), itemEntity.getY() + 0.2, itemEntity.getZ(), world.random.nextFloat() * 360.0f, 0.0f);
        allay.addCommandTag(NUISANCE_TAG);
        allay.setVelocity((world.random.nextDouble() - 0.5) * 0.2, 0.08 + world.random.nextDouble() * 0.1, (world.random.nextDouble() - 0.5) * 0.2);
        world.spawnEntity(allay);
    }

    public static boolean isNuisance(AllayEntity allay) {
        return !allay.getCommandTags().contains(NUISANCE_TAMED_TAG);
    }

    public static boolean isGloating(AllayEntity allay) {
        return allay.getWorld().getTime() <= getGloatUntil(allay);
    }

    public static void ensureNuisanceTag(AllayEntity allay) {
        if (allay.getCommandTags().contains(NUISANCE_TAMED_TAG)) return;
        if (!allay.getCommandTags().contains(NUISANCE_TAG)) {
            allay.addCommandTag(NUISANCE_TAG);
            if (allay.getWorld() instanceof ServerWorld world) {
                // Initial spawn panic to run and hide
                setPanicUntil(allay, world.getTime() + PANIC_SPRINT_TICKS * 2);
            }
        }
    }

    public static void tickNuisanceAllay(AllayEntity allay) {
        if (!(allay.getWorld() instanceof ServerWorld world)) return;
        if (!isNuisance(allay) || !allay.isAlive()) return;

        long now = world.getTime();
        long staggeredTick = now + allay.getId();
        PlayerEntity nearest = selectFocusPlayer(allay, world);

        boolean holdingItem = !allay.getMainHandStack().isEmpty();
        boolean chasing = nearest != null && isPlayerChasing(allay, nearest, now);
        
        if (nearest == null || (!chasing && !holdingItem)) {
            int boredom = getBoredom(allay) + 1;
            if (boredom > MAX_BOREDOM_TICKS) {
                allay.discard();
                return;
            }
            setBoredom(allay, boredom);
        } else {
            setBoredom(allay, 0);
        }

        if (isClimbActive(allay, now)) {
            if (nearest != null) {
                moveClimb(allay, nearest, SPEED_HIDE, now);
            }
            return;
        }

        if (isPanicActive(allay, now)) {
            if (nearest != null) {
                moveRunAway(allay, nearest, SPEED_PANIC, now, holdingItem);
            }
            return;
        }

        if (holdingItem) {
            maybeExpireStolenItem(allay, world);
            holdingItem = !allay.getMainHandStack().isEmpty();
        }

        if (holdingItem) {
            tickHoldingBehavior(allay, world, nearest, now, staggeredTick);
            emitDebugHeadEffect(allay, world, nearest, now);
            return;
        }

        clearHoldingStateTags(allay);
        tickStalkingBehavior(allay, world, nearest, now, staggeredTick);
        emitDebugHeadEffect(allay, world, nearest, now);
    }

    private static void tickHoldingBehavior(AllayEntity allay, ServerWorld world, PlayerEntity nearest, long now, long staggeredTick) {
        PlayerEntity targetPlayer = nearest;
        if (targetPlayer == null) {
            targetPlayer = getStolenPlayer(world, allay);
        }
        if (targetPlayer == null) {
            targetPlayer = selectFocusPlayer(allay, world);
        }
        nearest = targetPlayer;

        if (nearest == null) {
            moveHiddenDrift(allay, world, SPEED_REST);
            return;
        }

        if (allay.getCommandTags().contains(CHEST_WAIT_GLOAT_TAG)) {
            if (isAnyNearbyPlayerInContainer(world, allay, 14.0)) {
                // #region agent log
                debugLog(
                    allay,
                    "H18",
                    "NuisanceAllayHelper#tickHoldingBehavior",
                    "Holding chest-wait uses flee leg",
                    "{\"entityId\":" + allay.getId() + ",\"playerInContainer\":true}"
                );
                // #endregion
                moveRunAway(allay, nearest, SPEED_FLEE + 0.12, now, true);
                return;
            }
            allay.removeCommandTag(CHEST_WAIT_GLOAT_TAG);
            setGloatUntil(allay, now + GLOAT_TICKS);
        }

        long stolenAt = getStolenTimestamp(allay);
        long elapsed = stolenAt < 0 ? 0L : Math.max(0L, now - stolenAt);
        boolean freshLootGetaway = elapsed < POST_STEAL_GETAWAY_TICKS
            || (allay.getCommandTags().contains(PLAYER_LOOT_STEAL_TAG) && elapsed < PLAYER_LOOT_PRE_GLOAT_GETAWAY_TICKS);
        boolean initialStealGloatWindow = elapsed <= POST_STEAL_INITIAL_GLOAT_TICKS;

        // Holding-item loop should always remain in the playful flee/taunt sequence.
        // A rest gate here starves gloat branches and causes drive-by behavior.

        if (freshLootGetaway) {
            moveRunAway(allay, nearest, SPEED_FLEE + 0.14, now, true);
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H11",
                    "NuisanceAllayHelper#tickHoldingBehavior",
                    "Fresh loot getaway fleeing",
                    "{\"entityId\":" + allay.getId() + ",\"elapsed\":" + elapsed + "}"
                );
                // #endregion
            }
            return;
        }

        long gloatUntil = getGloatUntil(allay);
        if (gloatUntil >= 0L && now > gloatUntil) {
            setLastGloatEnd(allay, gloatUntil);
            clearGloatUntilTag(allay);
        }

        boolean chasing = isPlayerChasing(allay, nearest, now);
        double distanceSq = allay.squaredDistanceTo(nearest);
        boolean playerClosing = isPlayerClosingDistance(allay, nearest);
        boolean recentSteal = elapsed <= RECENT_STEAL_TAUNT_WINDOW_TICKS;
        boolean safeForGloat = distanceSq >= RE_GLOAT_MIN_DISTANCE * RE_GLOAT_MIN_DISTANCE;
        boolean gloatActive = isGloating(allay);
        boolean postGloatCooldownActive = now - getLastGloatEnd(allay) < POST_GLOAT_REPOSITION_TICKS;
        boolean inTeaseRange = distanceSq >= TAUNT_TEASE_MIN_DISTANCE * TAUNT_TEASE_MIN_DISTANCE
            && distanceSq <= TAUNT_TEASE_MAX_DISTANCE * TAUNT_TEASE_MAX_DISTANCE;
        boolean eligibleToTaunt = recentSteal && inTeaseRange && !chasing && !playerClosing;
        
        // Guaranteed short laugh/taunt right after a steal, unless the player is already pressuring.
        if (initialStealGloatWindow && !chasing && safeForGloat) {
            if ((allay.age + allay.getId()) % 10 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H20",
                    "NuisanceAllayHelper#tickHoldingBehavior",
                    "Initial gloat branch entered",
                    "{\"entityId\":" + allay.getId() + ",\"elapsed\":" + elapsed + ",\"distanceSq\":" + distanceSq + ",\"chasing\":" + chasing + ",\"safeForGloat\":" + safeForGloat + ",\"velocitySq\":" + allay.getVelocity().lengthSquared() + "}"
                );
                // #endregion
            }
            moveGloatInPlace(allay, nearest, world, SPEED_GLOAT_BOB);
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H10",
                    "NuisanceAllayHelper#tickHoldingBehavior",
                    "Initial post-steal gloat window active",
                    "{\"entityId\":" + allay.getId() + ",\"elapsed\":" + elapsed + ",\"distanceSq\":" + distanceSq + "}"
                );
                // #endregion
            }
            return;
        }

        if (eligibleToTaunt && !isOrbitActive(allay, now)) {
            setOrbitUntil(allay, now + TAUNT_ORBIT_TICKS);
        }

        if (isOrbitActive(allay, now)) {
            moveTauntOrbit(allay, nearest, world, SPEED_GLOAT_BOB + 0.06);
            return;
        }

        // Re-engage for a short tease if they stop chasing and drift too far.
        if (recentSteal && !chasing && distanceSq > TAUNT_REENGAGE_DISTANCE * TAUNT_REENGAGE_DISTANCE) {
            moveReengageTease(allay, nearest, world, SPEED_HIDE + 0.05);
            return;
        }

        // Spyro loop: if not pressured and in tease range, taunt in the player's face.
        boolean shouldGloat = !postGloatCooldownActive && (eligibleToTaunt || (!chasing && !playerClosing && safeForGloat));

        // While gloating, lock into stationary taunt and do not flee until the gloat window closes.
        if (gloatActive) {
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H26",
                    "NuisanceAllayHelper#tickHoldingBehavior",
                    "Gloat hold enforced",
                    "{\"entityId\":" + allay.getId() + ",\"distanceSq\":" + distanceSq + ",\"chasing\":" + chasing + ",\"playerClosing\":" + playerClosing + "}"
                );
                // #endregion
            }
            moveGloatInPlace(allay, nearest, world, SPEED_GLOAT_BOB);
            return;
        }

        // If player is actively chasing, closing in, or still too close, keep running until safe.
        if (chasing || playerClosing || !safeForGloat || distanceSq < FLEE_HARD_DISTANCE * FLEE_HARD_DISTANCE) {
            moveRunAway(allay, nearest, SPEED_FLEE + 0.12, now, true);
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H11",
                    "NuisanceAllayHelper#tickHoldingBehavior",
                    "Post-gloat flee until safe distance",
                    "{\"entityId\":" + allay.getId() + ",\"distanceSq\":" + distanceSq + ",\"safeForGloat\":" + safeForGloat + ",\"chasing\":" + chasing + ",\"playerClosing\":" + playerClosing + "}"
                );
                // #endregion
            }
            return;
        }

        // Player stopped chasing: gloat in place and taunt!
        if (shouldGloat) {
            if (!gloatActive) {
                // #region agent log
                debugLog(
                    allay,
                    "H31",
                    "NuisanceAllayHelper#tickHoldingBehavior",
                    "Re-opened gloat window after cooldown",
                    "{\"entityId\":" + allay.getId() + ",\"distanceSq\":" + distanceSq + ",\"cooldownActive\":" + postGloatCooldownActive + "}"
                );
                // #endregion
                setGloatUntil(allay, now + GLOAT_TICKS);
            }
            moveGloatInPlace(allay, nearest, world, SPEED_GLOAT_BOB);
            return;
        }

        if (postGloatCooldownActive && !chasing && !playerClosing) {
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H31",
                    "NuisanceAllayHelper#tickHoldingBehavior",
                    "Post-gloat cooldown repositioning",
                    "{\"entityId\":" + allay.getId() + ",\"distanceSq\":" + distanceSq + "}"
                );
                // #endregion
            }
            moveReengageTease(allay, nearest, world, SPEED_HIDE + 0.06);
            return;
        }

        // Holding-item chase should ignore LOS stealth logic entirely.
        // Keep a taunt ring and flee only when pressured/too close.
        if (distanceSq < FLEE_REENGAGE_DISTANCE * FLEE_REENGAGE_DISTANCE || playerClosing || chasing) {
            moveRunAway(allay, nearest, SPEED_FLEE + 0.10, now, true);
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H16",
                    "NuisanceAllayHelper#tickHoldingBehavior",
                    "Holding chase loop: flee leg",
                    "{\"entityId\":" + allay.getId() + ",\"distanceSq\":" + distanceSq + ",\"chasing\":" + chasing + ",\"playerClosing\":" + playerClosing + "}"
                );
                // #endregion
            }
        } else {
            moveReengageTease(allay, nearest, world, SPEED_HIDE + 0.08);
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H16",
                    "NuisanceAllayHelper#tickHoldingBehavior",
                    "Holding chase loop: tease leg",
                    "{\"entityId\":" + allay.getId() + ",\"distanceSq\":" + distanceSq + "}"
                );
                // #endregion
            }
        }
    }

    private static void tickStalkingBehavior(AllayEntity allay, ServerWorld world, PlayerEntity nearest, long now, long staggeredTick) {
        if (nearest == null) {
            moveHiddenDrift(allay, world, SPEED_REST);
            return;
        }

        boolean chestDistracted = nearest.currentScreenHandler != nearest.playerScreenHandler;
        double lookDot = getPlayerLookDotToAllay(nearest, allay);
        boolean seen = isSeenByPlayer(nearest, allay);
        if ((allay.age + allay.getId()) % 20 == 0) {
            // #region agent log
            debugLog(
                allay,
                "H36",
                "NuisanceAllayHelper#tickStalkingBehavior",
                "Distract-state signals during stalking",
                "{\"entityId\":" + allay.getId()
                    + ",\"chestDistracted\":" + chestDistracted
                    + ",\"seen\":" + seen
                    + ",\"pitch\":" + nearest.getPitch()
                    + ",\"playerVelSq\":" + nearest.getVelocity().lengthSquared()
                    + "}"
            );
            // #endregion
        }

        double distanceSq = allay.squaredDistanceTo(nearest);
        boolean chasing = isPlayerChasing(allay, nearest, now);
        boolean immediateStealWindow = hasImmediateStealTarget(allay, world);
        boolean chestStealWindow = hasOpenChestWindowNearby(world, allay, CHEST_SCAN_RANGE);
        boolean stealCheckWindow = immediateStealWindow || (staggeredTick % STEAL_CHECK_TICKS == 0L);

        // Always attempt immediate steals, even under pressure.
        if (immediateStealWindow && tryStealGroundItem(allay, world, nearest, false)) {
            return;
        }

        // Core stealing behavior is throttled but frequent for responsiveness.
        if (stealCheckWindow) {
            boolean allowStealPathing = !chasing || allay.squaredDistanceTo(nearest) > FLEE_HARD_DISTANCE * FLEE_HARD_DISTANCE * 1.6;
            if (tryStealGroundItem(allay, world, nearest, allowStealPathing)) {
                return;
            }
        }

        // Chest steals remain opportunistic; allow while distracted or from safer angles.
        if (stealCheckWindow && (chestStealWindow || !seen || chestDistracted || !chasing)) {
            boolean stoleChest = tryStealFromChest(allay, world, nearest, true, chestDistracted);
            if (stoleChest) return;
        }

        if (chasing || (lookDot >= DIRECT_STARE_DOT && distanceSq < STALK_MAX_DISTANCE * STALK_MAX_DISTANCE)) {
            // Hard reaction when directly stared at or actively chased.
            moveRunAway(allay, nearest, SPEED_PANIC + 0.10, now, false);
            return;
        }

        if (seen) {
            if (moveStalkWithCover(allay, nearest, world, SPEED_HIDE + 0.08, now)) {
                return;
            }
            moveHideFromView(allay, nearest, SPEED_HIDE + 0.08, now);
        } else {
            double stalkSpeed = SPEED_SNEAK + 0.08;
            moveSneakPatrol(allay, nearest, world, stalkSpeed);
        }
    }

    public static PlayerEntity selectFocusPlayer(AllayEntity allay, ServerWorld world) {
        PlayerEntity nearest = null;
        double nearestDistSq = Double.MAX_VALUE;
        PlayerEntity nearestAny = null;
        double nearestAnyDistSq = Double.MAX_VALUE;

        List<AllayEntity> nearbyAllays = world.getEntitiesByType(
            EntityType.ALLAY,
            allay.getBoundingBox().expand(PLAYER_INTEREST_RANGE),
            a -> a != allay && isNuisance(a)
        );

        for (PlayerEntity p : world.getPlayers()) {
            if (p.isSpectator()) continue;
            double distSq = p.squaredDistanceTo(allay);

            // Hard focus guarantee: track nearest non-spectator regardless of range.
            if (distSq < nearestAnyDistSq) {
                nearestAnyDistSq = distSq;
                nearestAny = p;
            }

            if (distSq > PLAYER_INTEREST_RANGE * PLAYER_INTEREST_RANGE) continue;

            boolean hasOtherAllay = false;
            for (AllayEntity other : nearbyAllays) {
                if (other.squaredDistanceTo(p) < 16.0 * 16.0) {
                    hasOtherAllay = true;
                    break;
                }
            }

            // Spread nuisance pressure across players without making allays go idle.
            if (hasOtherAllay) distSq += TAKING_TURNS_PLAYER_PENALTY;

            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = p;
            }
        }

        if (nearest != null && nearestDistSq <= PLAYER_INTEREST_RANGE * PLAYER_INTEREST_RANGE) {
            return nearest;
        }

        // No in-range candidate: force target lock to the nearest available player.
        return nearestAny;
    }

    public static void runHoldingBehavior(AllayEntity allay, ServerWorld world, PlayerEntity focusPlayer, long now) {
        setDebugState(allay, "hold");
        // SBL holding path must also process expiry; tickNuisanceAllay is bypassed in this mode.
        maybeExpireStolenItem(allay, world);
        if (allay.getMainHandStack().isEmpty()) {
            return;
        }
        tickHoldingBehavior(allay, world, focusPlayer, now, now + allay.getId());
    }

    public static void runStealBehavior(AllayEntity allay, ServerWorld world, PlayerEntity focusPlayer, long now) {
        setDebugState(allay, "steal");
        boolean immediateStealWindow = hasImmediateStealTarget(allay, world);
        boolean closeCommitStealWindow = hasCloseStealTarget(allay, world, CLOSE_COMMIT_STEAL_RANGE);
        boolean anyStealTargetWindow = hasCloseStealTarget(allay, world, STEAL_ITEM_RANGE);
        boolean chestStealWindow = hasOpenChestWindowNearby(world, allay, CHEST_SCAN_RANGE);
        boolean stealCheckWindow = immediateStealWindow || ((now + allay.getId()) % STEAL_CHECK_TICKS == 0L);
        boolean chasing = focusPlayer != null && isPlayerChasing(allay, focusPlayer, now);
        boolean chestDistracted = focusPlayer != null && focusPlayer.currentScreenHandler != focusPlayer.playerScreenHandler;
        boolean seen = focusPlayer != null && isSeenByPlayer(focusPlayer, allay);

        if (focusPlayer != null && (allay.age + allay.getId()) % 20 == 0) {
            // #region agent log
            debugLog(
                allay,
                "H36",
                "NuisanceAllayHelper#runStealBehavior",
                "Distract-state signals during steal",
                "{\"entityId\":" + allay.getId()
                    + ",\"chestDistracted\":" + chestDistracted
                    + ",\"seen\":" + seen
                    + ",\"pitch\":" + focusPlayer.getPitch()
                    + ",\"playerVelSq\":" + focusPlayer.getVelocity().lengthSquared()
                    + "}"
            );
            // #endregion
        }

        if (immediateStealWindow && tryStealGroundItem(allay, world, focusPlayer, false)) {
            // #region agent log
            debugLog(
                allay,
                "H7",
                "NuisanceAllayHelper#runStealBehavior",
                "Immediate swoop steal executed",
                "{\"entityId\":" + allay.getId() + "}"
            );
            // #endregion
            return;
        }

        // Priority rule: until a steal is immediately available, avoid line-of-sight above all else.
        // Only relax this when the player is distracted in a container screen.
        if (focusPlayer != null && seen && !chestDistracted && !closeCommitStealWindow && !anyStealTargetWindow && !chestStealWindow) {
            setDebugState(allay, "swoop_wait");
            runHideOutOfSightBehavior(allay, focusPlayer, world, now);
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H9",
                    "NuisanceAllayHelper#runStealBehavior",
                    "LOS avoid priority gate active",
                    "{\"entityId\":" + allay.getId() + ",\"seen\":" + seen + ",\"chasing\":" + chasing + ",\"chestDistracted\":" + chestDistracted + "}"
                );
                // #endregion
            }
            return;
        }
        if (chestStealWindow && (allay.age + allay.getId()) % 20 == 0) {
            // #region agent log
            debugLog(
                allay,
                "H33",
                "NuisanceAllayHelper#runStealBehavior",
                "Chest window bypassed LOS steal gate",
                "{\"entityId\":" + allay.getId() + ",\"seen\":" + seen + ",\"chestDistracted\":" + chestDistracted + "}"
            );
            // #endregion
        }
        if (focusPlayer != null && seen && (closeCommitStealWindow || anyStealTargetWindow) && (allay.age + allay.getId()) % 20 == 0) {
            // #region agent log
            debugLog(
                allay,
                "H14",
                "NuisanceAllayHelper#runStealBehavior",
                "LOS gate bypassed for close commit steal",
                "{\"entityId\":" + allay.getId() + ",\"closeCommitStealWindow\":" + closeCommitStealWindow + ",\"anyStealTargetWindow\":" + anyStealTargetWindow + "}"
            );
            // #endregion
        }

        if (stealCheckWindow) {
            boolean aggressiveCommitPathing = anyStealTargetWindow && focusPlayer != null
                && allay.squaredDistanceTo(focusPlayer) <= (STEAL_ITEM_RANGE + 6.0) * (STEAL_ITEM_RANGE + 6.0);
            boolean allowStealPathing = aggressiveCommitPathing
                || !chasing
                || (focusPlayer != null && allay.squaredDistanceTo(focusPlayer) > FLEE_HARD_DISTANCE * FLEE_HARD_DISTANCE * 1.6);
            if (aggressiveCommitPathing && (allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H17",
                    "NuisanceAllayHelper#runStealBehavior",
                    "Aggressive commit pathing enabled",
                    "{\"entityId\":" + allay.getId() + ",\"allowStealPathing\":" + allowStealPathing + "}"
                );
                // #endregion
            }
            if (tryStealGroundItem(allay, world, focusPlayer, allowStealPathing)) {
                // #region agent log
                debugLog(
                    allay,
                    "H7",
                    "NuisanceAllayHelper#runStealBehavior",
                    "Ground-item steal path committed",
                    "{\"entityId\":" + allay.getId() + ",\"allowPathing\":" + allowStealPathing + "}"
                );
                // #endregion
                return;
            }
        }
        if (stealCheckWindow && (chestStealWindow || focusPlayer == null || !seen || chestDistracted || !chasing)) {
            if (tryStealFromChest(allay, world, focusPlayer, true, chestDistracted)) {
                // #region agent log
                debugLog(
                    allay,
                    "H7",
                    "NuisanceAllayHelper#runStealBehavior",
                    "Chest steal executed",
                    "{\"entityId\":" + allay.getId() + "}"
                );
                // #endregion
                return;
            }
        }

        // No viable steal right now: keep stalking/hiding in place instead of aimless flying.
        if (focusPlayer == null) {
            setDebugState(allay, "swoop_wait");
            runIdleDriftBehavior(allay, world);
            return;
        }

        if (seen || chasing) {
            setDebugState(allay, "swoop_wait");
            runHideOutOfSightBehavior(allay, focusPlayer, world, now);
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H7",
                    "NuisanceAllayHelper#runStealBehavior",
                    "Swoop wait in hide posture",
                    "{\"entityId\":" + allay.getId() + ",\"seen\":" + seen + ",\"chasing\":" + chasing + "}"
                );
                // #endregion
            }
            return;
        }

        setDebugState(allay, "swoop_wait");
        if (!runStalkWithCoverBehavior(allay, focusPlayer, world, now)) {
            runStalkPatrolBehavior(allay, focusPlayer, world);
        }
        if ((allay.age + allay.getId()) % 20 == 0) {
            // #region agent log
            debugLog(
                allay,
                "H7",
                "NuisanceAllayHelper#runStealBehavior",
                "Swoop wait in stalking posture",
                "{\"entityId\":" + allay.getId() + "}"
            );
            // #endregion
        }
    }

    public static void runFleeBehavior(AllayEntity allay, PlayerEntity focusPlayer, long now) {
        setDebugState(allay, "flee");
        moveRunAway(allay, focusPlayer, SPEED_PANIC + 0.10, now, !allay.getMainHandStack().isEmpty());
    }

    public static void runHideOutOfSightBehavior(AllayEntity allay, PlayerEntity focusPlayer, ServerWorld world, long now) {
        setDebugState(allay, "hide");
        double distSq = allay.squaredDistanceTo(focusPlayer);
        boolean emergencyClose = distSq <= EMERGENCY_HIDE_DISTANCE * EMERGENCY_HIDE_DISTANCE;
        if (emergencyClose) {
            if (moveToNearestCover(allay, focusPlayer, world, SPEED_HIDE + 0.26, now, true)) {
                if ((allay.age + allay.getId()) % 20 == 0) {
                    // #region agent log
                    debugLog(
                        allay,
                        "H13",
                        "NuisanceAllayHelper#runHideOutOfSightBehavior",
                        "Emergency nearest-cover escape used",
                        "{\"entityId\":" + allay.getId() + ",\"distSq\":" + distSq + "}"
                    );
                    // #endregion
                }
                return;
            }
            moveDirectAwayFromPlayer(allay, focusPlayer, SPEED_HIDE + 0.28, now);
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H13",
                    "NuisanceAllayHelper#runHideOutOfSightBehavior",
                    "Emergency direct-away fallback used",
                    "{\"entityId\":" + allay.getId() + ",\"distSq\":" + distSq + "}"
                );
                // #endregion
            }
            return;
        }

        if (moveToNearestCover(allay, focusPlayer, world, SPEED_HIDE + 0.12, now, false)) {
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H13",
                    "NuisanceAllayHelper#runHideOutOfSightBehavior",
                    "Nearest-cover escape used",
                    "{\"entityId\":" + allay.getId() + ",\"distSq\":" + distSq + "}"
                );
                // #endregion
            }
            return;
        }

        boolean usedCover = moveStalkWithCover(allay, focusPlayer, world, SPEED_HIDE + 0.08, now);
        if ((allay.age + allay.getId()) % 20 == 0) {
            // #region agent log
            debugLog(
                allay,
                "H12",
                "NuisanceAllayHelper#runHideOutOfSightBehavior",
                "Hide branch selection",
                "{\"entityId\":" + allay.getId() + ",\"usedCover\":" + usedCover + ",\"distSq\":" + allay.squaredDistanceTo(focusPlayer) + "}"
            );
            // #endregion
        }
        if (usedCover) return;
        moveHideFromView(allay, focusPlayer, SPEED_HIDE + 0.08, now);
    }

    private static boolean moveToNearestCover(AllayEntity allay, PlayerEntity player, ServerWorld world, double speed, long now, boolean emergency) {
        Vec3d origin = allay.getPos();
        Vec3d best = null;
        double bestTravelSq = Double.MAX_VALUE;
        double currentDistSq = allay.squaredDistanceTo(player);
        Vec3d anchor = emergency ? origin : player.getPos();
        double[] radii = emergency ? new double[] {1.8, 2.6, 3.6, 4.8, 6.0} : new double[] {6.0, 8.0, 10.0, 12.0};

        for (double radius : radii) {
            for (int deg = 0; deg < 360; deg += 30) {
                double rad = Math.toRadians(deg + (allay.getId() % 15));
                Vec3d candidate = anchor.add(Math.cos(rad) * radius, 0.35, Math.sin(rad) * radius);
                candidate = clampTargetHeight(candidate, player, HEIGHT_BAND_MIN_ABOVE_PLAYER, HEIGHT_BAND_MAX_ABOVE_PLAYER);
                if (!hasLineOfSightCover(world, player, candidate)) continue;

                double candidateDistSq = player.squaredDistanceTo(candidate);
                if (candidateDistSq < currentDistSq * 0.92) continue;
                if (!emergency && candidateDistSq > MAX_HIDE_RING_DISTANCE * MAX_HIDE_RING_DISTANCE) continue;

                double travelSq = origin.squaredDistanceTo(candidate);
                if (travelSq < bestTravelSq) {
                    bestTravelSq = travelSq;
                    best = candidate;
                }
            }
            if (best != null) break;
        }

        if (best == null) return false;
        moveTo(allay, best, speed, now);
        return true;
    }

    private static void moveDirectAwayFromPlayer(AllayEntity allay, PlayerEntity player, double speed, long now) {
        Vec3d away = flattenHorizontal(allay.getPos().subtract(player.getPos()));
        if (away.lengthSquared() < 0.0001) away = new Vec3d(0.01, 0.0, 0.01);
        Vec3d dir = away.normalize();
        Vec3d right = getRightVector(dir);
        double sidestep = ((allay.getId() & 1) == 0 ? 1.0 : -1.0) * 0.8;
        Vec3d target = allay.getPos().add(dir.multiply(5.6)).add(right.multiply(sidestep));
        target = clampTargetHeight(target, player, HEIGHT_BAND_MIN_ABOVE_PLAYER, HEIGHT_BAND_MAX_ABOVE_PLAYER);
        moveTo(allay, target, speed, now);
    }

    public static boolean runStalkWithCoverBehavior(AllayEntity allay, PlayerEntity focusPlayer, ServerWorld world, long now) {
        setDebugState(allay, "stalk_cover");
        return moveStalkWithCover(allay, focusPlayer, world, SPEED_HIDE + 0.04, now);
    }

    public static void runStalkPatrolBehavior(AllayEntity allay, PlayerEntity focusPlayer, ServerWorld world) {
        setDebugState(allay, "stalk");
        moveSneakPatrol(allay, focusPlayer, world, SPEED_SNEAK + 0.08);
    }

    public static void runIdleDriftBehavior(AllayEntity allay, ServerWorld world) {
        setDebugState(allay, "idle");
        moveHiddenDrift(allay, world, SPEED_REST);
    }

    public static void setDebugState(AllayEntity allay, String state) {
        clearTagsByPrefix(allay, DEBUG_STATE_TAG_PREFIX);
        allay.addCommandTag(DEBUG_STATE_TAG_PREFIX + state);
    }

    public static void debugLog(AllayEntity allay, String hypothesisId, String location, String message, String dataJson) {
        if (!DEBUG_RUNTIME_LOGGING_ENABLED) return;
        String line = "{"
            + "\"sessionId\":\"" + DEBUG_SESSION_ID + "\","
            + "\"runId\":\"" + DEBUG_RUN_ID + "\","
            + "\"hypothesisId\":\"" + escapeJson(hypothesisId) + "\","
            + "\"location\":\"" + escapeJson(location) + "\","
            + "\"message\":\"" + escapeJson(message) + "\","
            + "\"data\":" + (dataJson == null ? "{}" : dataJson) + ","
            + "\"timestamp\":" + System.currentTimeMillis()
            + "}";
        try {
            Files.writeString(DEBUG_LOG_PATH, line + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {
        }
    }

    public static void debugLogGlobal(String hypothesisId, String location, String message, String dataJson) {
        if (!DEBUG_RUNTIME_LOGGING_ENABLED) return;
        String line = "{"
            + "\"sessionId\":\"" + DEBUG_SESSION_ID + "\","
            + "\"runId\":\"" + DEBUG_RUN_ID + "\","
            + "\"hypothesisId\":\"" + escapeJson(hypothesisId) + "\","
            + "\"location\":\"" + escapeJson(location) + "\","
            + "\"message\":\"" + escapeJson(message) + "\","
            + "\"data\":" + (dataJson == null ? "{}" : dataJson) + ","
            + "\"timestamp\":" + System.currentTimeMillis()
            + "}";
        try {
            Files.writeString(DEBUG_LOG_PATH, line + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {
        }
    }

    public static ItemEntity findBestGroundItemTarget(AllayEntity allay, ServerWorld world, PlayerEntity focusPlayer) {
        Box searchBox = allay.getBoundingBox().expand(STEAL_ITEM_RANGE);
        List<ItemEntity> items = world.getEntitiesByClass(
            ItemEntity.class,
            searchBox,
            item -> item.isAlive() && !item.cannotPickup() && isStealableItem(item.getStack())
        );
        if (items.isEmpty()) return null;

        ItemEntity best = null;
        double bestScore = Double.MAX_VALUE;

        for (ItemEntity item : items) {
            PlayerEntity owner = focusPlayer;
            if (owner == null || owner.squaredDistanceTo(item) > PLAYER_LOOT_FOCUS_RANGE * PLAYER_LOOT_FOCUS_RANGE) {
                owner = world.getClosestPlayer(item, PLAYER_LOOT_FOCUS_RANGE);
            }

            double allayDist = allay.squaredDistanceTo(item);
            int valueScore = getItemValueScore(item.getStack());
            double score;
            if (owner != null) {
                double ownerDist = owner.squaredDistanceTo(item);
                boolean freshLoot = item.getItemAge() <= PLAYER_KILL_LOOT_AGE_TICKS;
                double freshnessBoost = freshLoot ? (PLAYER_KILL_LOOT_AGE_TICKS - item.getItemAge()) / (double) PLAYER_KILL_LOOT_AGE_TICKS : 0.0;
                score = ownerDist * 0.75 + allayDist * 0.20 - (freshnessBoost * 10.0) - (valueScore * VALUE_PRIORITY_WEIGHT);
            } else {
                score = allayDist + 50.0 - (valueScore * VALUE_PRIORITY_WEIGHT);
            }

            if (score < bestScore) {
                bestScore = score;
                best = item;
            }
        }

        return best;
    }

    public static boolean hasNearbyStealableChestWindow(AllayEntity allay, ServerWorld world) {
        pruneExpiredChestWindows(world);
        BlockPos center = allay.getBlockPos();
        for (Map.Entry<ChestWindowKey, ChestOpenWindow> entry : OPENED_CHEST_WINDOWS.entrySet()) {
            if (entry.getKey().dimension != world.getRegistryKey()) continue;
            BlockPos pos = entry.getKey().pos;
            if (pos.getSquaredDistance(center) > CHEST_SCAN_RANGE * CHEST_SCAN_RANGE) continue;
            if (!isChestOpenWindowActive(world, pos)) continue;

            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof ChestBlockEntity chest)) continue;
            if (indexOfMostValuableStealableSlot(chest) >= 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValuableTamingItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.hasEnchantments()) return true;
        if (stack.isOf(Items.DIAMOND) || stack.isOf(Items.DIAMOND_BLOCK) || stack.isOf(Items.EMERALD) || stack.isOf(Items.NETHERITE_INGOT)) {
            return true;
        }
        return stack.getRarity() == Rarity.RARE || stack.getRarity() == Rarity.EPIC;
    }

    public static void tameNuisanceAllay(AllayEntity allay, PlayerEntity player, Hand hand) {
        ItemStack inHand = player.getStackInHand(hand);
        if (!player.getAbilities().creativeMode) {
            inHand.decrement(1);
        }

        allay.removeCommandTag(NUISANCE_TAG);
        allay.addCommandTag(NUISANCE_TAMED_TAG);
        allay.getNavigation().stop();
        clearAllStateTags(allay);

        if (!allay.getMainHandStack().isEmpty()) {
            allay.dropStack(allay.getMainHandStack().copy());
            allay.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }

        if (allay.getWorld() instanceof ServerWorld world) {
            world.spawnParticles(ParticleTypes.HEART, allay.getX(), allay.getY() + 0.4, allay.getZ(), 5, 0.2, 0.2, 0.2, 0.0);
            world.playSound(null, allay.getX(), allay.getY(), allay.getZ(), SoundEvents.ENTITY_ALLAY_AMBIENT_WITH_ITEM, SoundCategory.NEUTRAL, 0.9f, 1.1f);
        }
    }

    public static void onNuisanceDamaged(AllayEntity allay) {
        if (!(allay.getWorld() instanceof ServerWorld world)) return;
        if (!isNuisance(allay)) return;
        
        boolean holdingItem = !allay.getMainHandStack().isEmpty();
        long now = world.getTime();
        
        if (!holdingItem) {
            setClimbUntil(allay, now + CLIMB_TICKS);
            setPanicUntil(allay, now + CLIMB_TICKS + PANIC_SPRINT_TICKS);
        } else {
            // Just panic and run away horizontally, no climb, keep the item
            setPanicUntil(allay, now + PANIC_SPRINT_TICKS);
        }
    }

    private static boolean tryStealGroundItem(AllayEntity allay, ServerWorld world, PlayerEntity focusPlayer, boolean allowPathing) {
        Box searchBox = allay.getBoundingBox().expand(STEAL_ITEM_RANGE);
        List<ItemEntity> items = world.getEntitiesByClass(
            ItemEntity.class,
            searchBox,
            item -> item.isAlive() && !item.cannotPickup() && isStealableItem(item.getStack())
        );
        if (items.isEmpty()) return false;

        ItemEntity best = null;
        double bestScore = Double.MAX_VALUE;
        boolean bestFreshLoot = false;

        for (ItemEntity item : items) {
            PlayerEntity owner = focusPlayer;
            if (owner == null || owner.squaredDistanceTo(item) > PLAYER_LOOT_FOCUS_RANGE * PLAYER_LOOT_FOCUS_RANGE) {
                owner = world.getClosestPlayer(item, PLAYER_LOOT_FOCUS_RANGE);
            }

            double allayDist = allay.squaredDistanceTo(item);
            int valueScore = getItemValueScore(item.getStack());
            boolean freshLoot = false;
            double score;
            
            if (owner != null) {
                double ownerDist = owner.squaredDistanceTo(item);
                freshLoot = item.getItemAge() <= PLAYER_KILL_LOOT_AGE_TICKS;
                double freshnessBoost = freshLoot ? (PLAYER_KILL_LOOT_AGE_TICKS - item.getItemAge()) / (double) PLAYER_KILL_LOOT_AGE_TICKS : 0.0;
                // Heavily prioritize items close to the player (poacher behavior)
                score = ownerDist * 0.75 + allayDist * 0.20 - (freshnessBoost * 10.0) - (valueScore * VALUE_PRIORITY_WEIGHT);
            } else {
                // Items far from any player are much less interesting
                score = allayDist + 50.0 - (valueScore * VALUE_PRIORITY_WEIGHT);
            }

            if (score < bestScore) {
                bestScore = score;
                best = item;
                bestFreshLoot = freshLoot;
            }
        }

        if (best == null) return false;
        double distSq = allay.squaredDistanceTo(best);
        PlayerEntity stolenFrom = focusPlayer;
        if (stolenFrom == null || stolenFrom.squaredDistanceTo(best) > PLAYER_LOOT_FOCUS_RANGE * PLAYER_LOOT_FOCUS_RANGE) {
            stolenFrom = world.getClosestPlayer(best, PLAYER_LOOT_FOCUS_RANGE);
        }

        // Extremely aggressive close-range commit to beat player pickup reaction time.
        if (distSq <= CLOSE_COMMIT_INSTANT_GRAB_RANGE * CLOSE_COMMIT_INSTANT_GRAB_RANGE
            && Math.abs(allay.getY() - best.getY()) <= 0.95) {
                ItemStack stolen = best.getStack().split(getStealAmount(best.getStack()));
            if (!stolen.isEmpty()) {
                allay.setStackInHand(Hand.MAIN_HAND, stolen);
                setStolenTimestamp(allay, world.getTime(), best.getItemAge());
                setGloatUntil(allay, world.getTime() + GLOAT_TICKS);
                setStolenPlayer(allay, stolenFrom);
                if (bestFreshLoot) {
                    allay.addCommandTag(PLAYER_LOOT_STEAL_TAG);
                } else {
                    allay.removeCommandTag(PLAYER_LOOT_STEAL_TAG);
                }
                if (best.getStack().isEmpty()) {
                    best.discard();
                }
                world.playSound(
                    null,
                    allay.getX(),
                    allay.getY(),
                    allay.getZ(),
                    SoundEvents.ENTITY_ALLAY_ITEM_THROWN,
                    SoundCategory.NEUTRAL,
                    0.70f,
                    1.08f
                );
                // #region agent log
                debugLog(
                    allay,
                    "H24",
                    "NuisanceAllayHelper#tryStealGroundItem",
                    "Steal audio played",
                    "{\"event\":\"ENTITY_ALLAY_ITEM_THROWN\",\"entityId\":" + allay.getId() + "}"
                );
                // #endregion
                // #region agent log
                debugLog(
                    allay,
                    "H15",
                    "NuisanceAllayHelper#tryStealGroundItem",
                    "Close-commit instant steal executed",
                    "{\"entityId\":" + allay.getId() + ",\"distSq\":" + distSq + ",\"hasVictim\":" + (stolenFrom != null) + "}"
                );
                // #endregion
                return true;
            }
        }

        if (distSq <= IMMEDIATE_GRAB_RANGE * IMMEDIATE_GRAB_RANGE
            && Math.abs(allay.getY() - best.getY()) <= 0.95) {
            ItemStack stolen = best.getStack().split(getStealAmount(best.getStack()));
            if (stolen.isEmpty()) return false;

            allay.setStackInHand(Hand.MAIN_HAND, stolen);
            setStolenTimestamp(allay, world.getTime(), best.getItemAge());
            setGloatUntil(allay, world.getTime() + GLOAT_TICKS);
            setStolenPlayer(allay, stolenFrom);
            if (bestFreshLoot) {
                allay.addCommandTag(PLAYER_LOOT_STEAL_TAG);
            } else {
                allay.removeCommandTag(PLAYER_LOOT_STEAL_TAG);
            }
            if (best.getStack().isEmpty()) {
                best.discard();
            }
            world.playSound(
                null,
                allay.getX(),
                allay.getY(),
                allay.getZ(),
                SoundEvents.ENTITY_ALLAY_ITEM_THROWN,
                SoundCategory.NEUTRAL,
                0.70f,
                1.08f
            );
            // #region agent log
            debugLog(
                allay,
                "H24",
                "NuisanceAllayHelper#tryStealGroundItem",
                "Steal audio played",
                "{\"event\":\"ENTITY_ALLAY_ITEM_THROWN\",\"entityId\":" + allay.getId() + "}"
            );
            // #endregion
            return true;
        }

        if (!allowPathing) return false;
        
        // Swoop in with a mostly direct intercept; keep only a small lateral weave at longer range.
        long now = world.getTime();
        Vec3d toItem = best.getPos().subtract(allay.getPos());
        Vec3d dir = toItem.normalize();
        Vec3d right = getRightVector(dir);
        
        // Small weave keeps motion lively but avoids long orbiting.
        double weave = Math.sin((now * 0.55) + (allay.getId() * 0.5)) * 0.35;
        
        Vec3d target;
        if (distSq < CLOSE_COMMIT_STEAL_RANGE * CLOSE_COMMIT_STEAL_RANGE) { // Close enough: dive to the item plane
            target = new Vec3d(best.getX(), best.getY() + 0.03, best.getZ());
        } else {
            target = allay.getPos().add(dir.multiply(5.2)).add(right.multiply(weave));
        }
        target = clampStealHeight(target, best);
        moveTo(allay, target, SPEED_STEAL_PURSUIT, now);
        return true;
    }

    private static boolean tryStealFromChest(AllayEntity allay, ServerWorld world, PlayerEntity focusPlayer, boolean allowPathing, boolean playerDistracted) {
        if (!allowPathing) return false;
        if (focusPlayer != null && !playerDistracted && isSeenByPlayer(focusPlayer, allay)) {
            boolean chestWindowNearby = hasOpenChestWindowNearby(world, allay, CHEST_SCAN_RANGE);
            if (!chestWindowNearby) {
                // #region agent log
                debugLog(
                    allay,
                    "H34",
                    "NuisanceAllayHelper#tryStealFromChest",
                    "Chest steal blocked by direct sight while player not distracted",
                    "{\"entityId\":" + allay.getId() + ",\"playerDistracted\":" + playerDistracted + "}"
                );
                // #endregion
                return false;
            }
            // #region agent log
            debugLog(
                allay,
                "H35",
                "NuisanceAllayHelper#tryStealFromChest",
                "Chest-open LOS bypass active for close approach",
                "{\"entityId\":" + allay.getId() + "}"
            );
            // #endregion
        }

        pruneExpiredChestWindows(world);
        BlockPos center = allay.getBlockPos();
        ChestBlockEntity targetChest = null;
        int targetSlot = -1;
        double bestChestScore = Double.NEGATIVE_INFINITY;

        for (Map.Entry<ChestWindowKey, ChestOpenWindow> entry : OPENED_CHEST_WINDOWS.entrySet()) {
            if (entry.getKey().dimension != world.getRegistryKey()) continue;
            BlockPos pos = entry.getKey().pos;
            if (pos.getSquaredDistance(center) > CHEST_SCAN_RANGE * CHEST_SCAN_RANGE) continue;
            if (!isChestOpenWindowActive(world, pos)) continue;
            
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof ChestBlockEntity chest)) continue;

            int slot = indexOfMostValuableStealableSlot(chest);
            if (slot < 0) continue;
            double distSq = center.getSquaredDistance(pos);
            int valueScore = getItemValueScore(chest.getStack(slot));
            double chestScore = (valueScore * CHEST_VALUE_PRIORITY_WEIGHT) - (distSq * 0.30);
            if (chestScore > bestChestScore) {
                bestChestScore = chestScore;
                targetChest = chest;
                targetSlot = slot;
            }
        }

        if (targetChest == null || targetSlot < 0) {
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H34",
                    "NuisanceAllayHelper#tryStealFromChest",
                    "No active chest target found",
                    "{\"entityId\":" + allay.getId() + "}"
                );
                // #endregion
            }
            return false;
        }

        double bestDistanceSq = center.getSquaredDistance(targetChest.getPos());
        if (bestDistanceSq > 2.0 * 2.0) {
            BlockPos pos = targetChest.getPos();
            moveTo(allay, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), SPEED_STEAL_PURSUIT, world.getTime());
            if ((allay.age + allay.getId()) % 20 == 0) {
                // #region agent log
                debugLog(
                    allay,
                    "H34",
                    "NuisanceAllayHelper#tryStealFromChest",
                    "Moving to chest target",
                    "{\"entityId\":" + allay.getId() + ",\"distSq\":" + bestDistanceSq + "}"
                );
                // #endregion
            }
            return true;
        }

        ItemStack sourceStack = targetChest.getStack(targetSlot);
        ItemStack stolen = targetChest.removeStack(targetSlot, getStealAmount(sourceStack));
        if (stolen.isEmpty()) return false;
        targetChest.markDirty();
        allay.setStackInHand(Hand.MAIN_HAND, stolen);
        setStolenTimestamp(allay, world.getTime(), 0);
        setGloatUntil(allay, world.getTime() + GLOAT_TICKS);
        setStolenPlayer(allay, focusPlayer);
        allay.addCommandTag(CHEST_WAIT_GLOAT_TAG);
        world.playSound(
            null,
            allay.getX(),
            allay.getY(),
            allay.getZ(),
            SoundEvents.ENTITY_ALLAY_ITEM_THROWN,
            SoundCategory.NEUTRAL,
            0.70f,
            1.08f
        );
        // #region agent log
        debugLog(
            allay,
            "H24",
            "NuisanceAllayHelper#tryStealFromChest",
            "Steal audio played",
            "{\"event\":\"ENTITY_ALLAY_ITEM_THROWN\",\"entityId\":" + allay.getId() + "}"
        );
        // #endregion
        return true;
    }

    private static int getBoredom(AllayEntity allay) {
        for (String tag : allay.getCommandTags()) {
            if (tag.startsWith(BOREDOM_TAG_PREFIX)) {
                try { return Integer.parseInt(tag.substring(BOREDOM_TAG_PREFIX.length())); } catch (Exception ignored) {}
            }
        }
        return 0;
    }

    private static void setBoredom(AllayEntity allay, int boredom) {
        clearTagsByPrefix(allay, BOREDOM_TAG_PREFIX);
        allay.addCommandTag(BOREDOM_TAG_PREFIX + boredom);
    }

    private static void moveClimb(AllayEntity allay, PlayerEntity player, double speed, long now) {
        Vec3d away = flattenHorizontal(allay.getPos().subtract(player.getPos()));
        if (away.lengthSquared() < 0.0001) {
            away = new Vec3d(0.01, 0.0, 0.01);
        }
        Vec3d dir = away.normalize();
        
        // Target is high up and slowly drifting away
        Vec3d target = allay.getPos().add(dir.multiply(1.5)).add(0, 5.2, 0);
        target = clampTargetHeight(target, player, HEIGHT_BAND_MIN_ABOVE_PLAYER, HEIGHT_BAND_MAX_ABOVE_PLAYER + 8.0);
        moveTo(allay, target, speed, now);
    }

    private static void moveRunAway(AllayEntity allay, PlayerEntity player, double speed, long now, boolean holdingItem) {
        Vec3d away = flattenHorizontal(allay.getPos().subtract(player.getPos()));
        if (away.lengthSquared() < 0.0001) {
            away = new Vec3d(0.01, 0.0, 0.01);
        }
        Vec3d dir = away.normalize();
        Vec3d right = getRightVector(dir);
        
        double flankBias = ((allay.getId() & 1) == 0 ? 1.0 : -1.0) * 0.45;
        double sidePulse = (((now / 14L) + allay.getId()) & 1L) == 0L ? 1.0 : -1.0;
        double weave = Math.sin((now * 0.33) + (allay.getId() * 0.57)) * 0.9
                     + Math.cos((now * 0.15) + (allay.getId() * 0.2)) * 0.6
                     + (sidePulse * 1.25)
                     + flankBias;
                     
        Vec3d target = allay.getPos()
            .add(dir.multiply(5.0))
            .add(right.multiply(weave * 1.5));
            
        double minHeight = holdingItem ? HEIGHT_BAND_MIN_ABOVE_PLAYER - 0.5 : HEIGHT_BAND_MIN_ABOVE_PLAYER;
        double maxHeight = holdingItem ? HEIGHT_BAND_MAX_ABOVE_PLAYER * 0.5 : HEIGHT_BAND_MAX_ABOVE_PLAYER;
        
        target = clampTargetHeight(target, player, minHeight, maxHeight);
        // Run-state audio only; stalking remains silent.
        if (holdingItem && (now + allay.getId()) % 28L == 0L) {
            allay.getWorld().playSound(
                null,
                allay.getX(),
                allay.getY(),
                allay.getZ(),
                SoundEvents.ENTITY_ALLAY_AMBIENT_WITH_ITEM,
                SoundCategory.NEUTRAL,
                0.20f,
                1.00f + (allay.getRandom().nextFloat() * 0.10f)
            );
            // #region agent log
            debugLog(
                allay,
                "H28",
                "NuisanceAllayHelper#moveRunAway",
                "Run audio played",
                "{\"sound\":\"ENTITY_ALLAY_AMBIENT_WITH_ITEM\",\"entityId\":" + allay.getId() + "}"
            );
            // #endregion
        }
        if ((allay.age + allay.getId()) % 20 == 0) {
            // #region agent log
            debugLog(
                allay,
                "H29",
                "NuisanceAllayHelper#moveRunAway",
                "Run path weaving",
                "{\"entityId\":" + allay.getId() + ",\"weave\":" + weave + ",\"sidePulse\":" + sidePulse + "}"
            );
            // #endregion
        }
        moveTo(allay, target, speed, now);
    }

    private static void moveHideFromView(AllayEntity allay, PlayerEntity player, double speed, long now) {
        Vec3d look = flattenHorizontal(player.getRotationVec(1.0f));
        if (look.lengthSquared() < 0.0001) {
            look = new Vec3d(0.0, 0.0, 1.0);
        }
        look = look.normalize();
        Vec3d right = getRightVector(look);

        Vec3d toAllay = flattenHorizontal(allay.getPos().subtract(player.getPos()));
        double currentDist = toAllay.length();
        if (currentDist < 0.0001) {
            toAllay = new Vec3d(0.01, 0.0, 0.01);
            currentDist = toAllay.length();
        }
        
        // Which side of the look vector is the Allay on?
        double dotRight = toAllay.dotProduct(right);
        Vec3d escapeDir;
        if (dotRight >= 0) {
            // Allay is to the right, so move further right
            escapeDir = right;
        } else {
            // Allay is to the left, so move further left
            escapeDir = right.multiply(-1.0);
        }
        
        // Also push slightly away from the player
        Vec3d awayDir = toAllay.normalize();
        
        // Combine escape direction and away direction
        Vec3d moveDir = escapeDir.multiply(1.5).add(awayDir).normalize();
        
        // Ensure the target is actually moving AWAY from the player, not just sideways
        Vec3d target = allay.getPos().add(moveDir.multiply(4.0));
        
        // If the target would bring it closer to the player than it already is, force it backwards
        if (flattenHorizontal(target.subtract(player.getPos())).lengthSquared() < currentDist * currentDist) {
            target = allay.getPos().add(awayDir.multiply(4.0));
        }

        // Hard stand-off ring: never hide too close while the player is looking.
        Vec3d targetFromPlayer = flattenHorizontal(target.subtract(player.getPos()));
        double targetDist = targetFromPlayer.length();
        if (targetDist < HIDE_MIN_DISTANCE) {
            Vec3d safeDir = targetFromPlayer.lengthSquared() < 0.0001 ? awayDir : targetFromPlayer.normalize();
            target = player.getPos().add(safeDir.multiply(HIDE_PREFERRED_DISTANCE)).add(0.0, target.y - player.getY(), 0.0);
            targetDist = HIDE_PREFERRED_DISTANCE;
        }

        target = clampTargetHeight(target, player, HEIGHT_BAND_MIN_ABOVE_PLAYER, HEIGHT_BAND_MAX_ABOVE_PLAYER);
        if ((allay.age + allay.getId()) % 20 == 0) {
            double targetLookDot = flattenHorizontal(target.subtract(player.getPos())).normalize().dotProduct(look);
            // #region agent log
            debugLog(
                allay,
                "H8",
                "NuisanceAllayHelper#moveHideFromView",
                "Hide target distance clamp",
                "{\"entityId\":" + allay.getId() + ",\"holdingItem\":" + !allay.getMainHandStack().isEmpty() + ",\"currentDist\":" + currentDist + ",\"targetDist\":" + targetDist + ",\"targetLookDot\":" + targetLookDot + "}"
            );
            // #endregion
        }
        moveTo(allay, target, speed, now);
    }

    private static void moveSneakPatrol(AllayEntity allay, PlayerEntity player, ServerWorld world, double speed) {
        long now = world.getTime();
        Vec3d look = flattenHorizontal(player.getRotationVec(1.0f));
        if (look.lengthSquared() < 0.0001) {
            look = new Vec3d(0.0, 0.0, 1.0);
        }
        look = look.normalize();
        Vec3d right = getRightVector(look);

        // Predict where the player will be shortly and approach from behind + flank.
        Vec3d predictedPlayerPos = player.getPos().add(flattenHorizontal(player.getVelocity()).multiply(20.0 * STALK_PREDICTION_SECONDS));
        double sideSign = ((allay.getId() & 1) == 0) ? 1.0 : -1.0;
        double sideDrift = Math.sin((now * 0.07) + (allay.getId() * 0.41)) * 0.85;
        double sideOffset = sideSign * STALK_SIDE_AMPLITUDE + sideDrift;

        Vec3d target = predictedPlayerPos
            .add(look.multiply(-5.0))
            .add(right.multiply(sideOffset))
            .add(0.0, 0.30, 0.0);

        // Keep a strong stalking ring to avoid jittering in/out of panic ranges.
        Vec3d fromPlayer = flattenHorizontal(target.subtract(player.getPos()));
        double dist = fromPlayer.length();
        if (dist < 0.0001) {
            fromPlayer = look.multiply(-1.0);
            dist = 1.0;
        }
        if (dist < STALK_MIN_DISTANCE) {
            target = player.getPos().add(fromPlayer.normalize().multiply(STALK_MIN_DISTANCE));
        } else if (dist > STALK_MAX_DISTANCE) {
            target = player.getPos().add(fromPlayer.normalize().multiply(STALK_MAX_DISTANCE));
        }

        target = clampTargetHeight(target, player, HEIGHT_BAND_MIN_ABOVE_PLAYER, 2.0);
        moveTo(allay, target, speed, now);
    }

    private static boolean moveStalkWithCover(AllayEntity allay, PlayerEntity player, ServerWorld world, double speed, long now) {
        Vec3d look = flattenHorizontal(player.getRotationVec(1.0f));
        if (look.lengthSquared() < 0.0001) {
            look = new Vec3d(0.0, 0.0, 1.0);
        }
        look = look.normalize();
        Vec3d right = getRightVector(look);
        Vec3d predictedPlayerPos = player.getPos().add(flattenHorizontal(player.getVelocity()).multiply(7.0));

        Vec3d[] candidates = new Vec3d[] {
            predictedPlayerPos.add(look.multiply(-4.5)).add(right.multiply(2.8)).add(0.0, 0.35, 0.0),
            predictedPlayerPos.add(look.multiply(-4.5)).add(right.multiply(-2.8)).add(0.0, 0.35, 0.0),
            predictedPlayerPos.add(look.multiply(-6.2)).add(right.multiply(1.4)).add(0.0, 0.45, 0.0),
            predictedPlayerPos.add(look.multiply(-6.2)).add(right.multiply(-1.4)).add(0.0, 0.45, 0.0),
            predictedPlayerPos.add(look.multiply(-3.6)).add(right.multiply(3.6)).add(0.0, 0.25, 0.0),
            predictedPlayerPos.add(look.multiply(-3.6)).add(right.multiply(-3.6)).add(0.0, 0.25, 0.0)
        };

        Vec3d best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        int coverCandidateCount = 0;

        for (Vec3d candidateRaw : candidates) {
            Vec3d candidate = clampTargetHeight(candidateRaw, player, HEIGHT_BAND_MIN_ABOVE_PLAYER, 2.4);
            Vec3d toCandidate = flattenHorizontal(candidate.subtract(player.getPos()));
            double dist = toCandidate.length();
            if (dist < COVER_STALK_MIN_DISTANCE || dist > COVER_STALK_MAX_DISTANCE) continue;
            if (!hasLineOfSightCover(world, player, candidate)) continue;
            coverCandidateCount++;

            double distToAllay = allay.squaredDistanceTo(candidate);
            Vec3d behindDir = flattenHorizontal(player.getPos().subtract(candidate));
            double behindDot = behindDir.lengthSquared() < 0.0001 ? 1.0 : look.dotProduct(behindDir.normalize());

            // Prefer strong rear angles and reachable positions.
            double score = (behindDot * 5.0) - (distToAllay * 0.10);
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        if (best == null) return false;
        if ((allay.age + allay.getId()) % 20 == 0) {
            // #region agent log
            debugLog(
                allay,
                "H12",
                "NuisanceAllayHelper#moveStalkWithCover",
                "Cover target selected",
                "{\"entityId\":" + allay.getId() + ",\"coverCandidateCount\":" + coverCandidateCount + ",\"bestScore\":" + bestScore + "}"
            );
            // #endregion
        }
        moveTo(allay, best, speed, now);
        return true;
    }

    private static boolean hasLineOfSightCover(ServerWorld world, PlayerEntity player, Vec3d targetPos) {
        Vec3d from = player.getEyePos();
        Vec3d to = targetPos.add(0.0, 0.2, 0.0);
        HitResult hit = world.raycast(new RaycastContext(from, to, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
        if (hit.getType() != HitResult.Type.BLOCK) return false;
        if (!(hit instanceof BlockHitResult blockHit)) return false;
        BlockPos pos = blockHit.getBlockPos();
        var state = world.getBlockState(pos);
        if (state.isIn(BlockTags.LEAVES)) return true;
        return state.isSolidBlock(world, pos) || state.isOpaqueFullCube(world, pos);
    }

    private static void moveRestingHide(AllayEntity allay, PlayerEntity player, ServerWorld world, double speed) {
        if (isSeenByPlayer(player, allay)) {
            moveHideFromView(allay, player, speed, world.getTime());
            return;
        }
        moveSneakPatrol(allay, player, world, speed);
    }

    private static void moveGloatInPlace(AllayEntity allay, PlayerEntity player, ServerWorld world, double speed) {
        setDebugState(allay, "taunt");
        if ((allay.age + allay.getId()) % 10 == 0) {
            // #region agent log
            debugLog(
                allay,
                "H22",
                "NuisanceAllayHelper#moveGloatInPlace",
                "Executing moveGloatInPlace",
                "{\"entityId\":" + allay.getId() + ",\"speed\":" + speed + ",\"velocitySqBefore\":" + allay.getVelocity().lengthSquared() + "}"
            );
            // #endregion
        }
        double t = world.getTime() + (allay.getId() * 0.29);

        // Clamp carry-over momentum so the taunt reads as a stationary jitter/laugh.
        Vec3d v = allay.getVelocity();
        allay.setVelocity(v.x * 0.10, v.y * 0.35, v.z * 0.10);
        
        // Fast vertical bob to create a visible laugh/taunt motion.
        double bob = Math.sin(t * GLOAT_BOB_FREQUENCY * 2.2) * (GLOAT_BOB_AMPLITUDE * 1.35);
        
        // Stay in place horizontally and bounce vertically.
        Vec3d target = new Vec3d(allay.getX(), allay.getY() + bob, allay.getZ());
        target = clampTargetHeight(target, player, GLOAT_HEIGHT_MIN_ABOVE_PLAYER, GLOAT_HEIGHT_MAX_ABOVE_PLAYER);
        
        // Snap gaze up/down quickly while keeping focus on the victim.
        double lookYOffset = Math.sin(t * GLOAT_LOOK_BOB_FREQUENCY * 1.5) * GLOAT_LOOK_BOB_AMPLITUDE * 2.0;
        allay.getLookControl().lookAt(player.getX(), player.getEyeY() + lookYOffset, player.getZ(), 36.0f, 36.0f);
        maybePlayGloatLaughSound(allay, world);
        moveTo(allay, target, speed, world.getTime());
    }

    private static void moveTauntOrbit(AllayEntity allay, PlayerEntity player, ServerWorld world, double speed) {
        setDebugState(allay, "orbit");
        double t = (world.getTime() * 0.26) + (allay.getId() * 0.8);
        Vec3d toAllay = flattenHorizontal(allay.getPos().subtract(player.getPos()));
        if (toAllay.lengthSquared() < 0.0001) {
            toAllay = new Vec3d(1.0, 0.0, 0.0);
        }
        Vec3d radial = toAllay.normalize();
        Vec3d tangential = getRightVector(radial);
        Vec3d orbitOffset = radial.multiply(TAUNT_ORBIT_RADIUS).add(tangential.multiply(Math.sin(t) * 1.2));
        double bob = Math.sin(t * GLOAT_BOB_FREQUENCY * 2.4) * GLOAT_BOB_AMPLITUDE;

        Vec3d target = player.getPos().add(orbitOffset).add(0.0, 2.4 + bob, 0.0);
        target = clampTargetHeight(target, player, GLOAT_HEIGHT_MIN_ABOVE_PLAYER, GLOAT_HEIGHT_MAX_ABOVE_PLAYER);
        allay.getLookControl().lookAt(player.getX(), player.getEyeY() + Math.sin(t * 3.2) * GLOAT_LOOK_BOB_AMPLITUDE, player.getZ(), 38.0f, 38.0f);
        moveTo(allay, target, speed, world.getTime());
    }

    private static void moveReengageTease(AllayEntity allay, PlayerEntity player, ServerWorld world, double speed) {
        setDebugState(allay, "reengage");
        Vec3d look = flattenHorizontal(player.getRotationVec(1.0f));
        if (look.lengthSquared() < 0.0001) {
            look = new Vec3d(0.0, 0.0, 1.0);
        }
        look = look.normalize();
        Vec3d right = getRightVector(look);
        double side = ((allay.getId() & 1) == 0 ? 1.0 : -1.0) * 2.4;
        Vec3d target = player.getPos()
            .add(look.multiply(-4.5))
            .add(right.multiply(side))
            .add(0.0, 2.2, 0.0);
        target = clampTargetHeight(target, player, GLOAT_HEIGHT_MIN_ABOVE_PLAYER, GLOAT_HEIGHT_MAX_ABOVE_PLAYER);
        moveTo(allay, target, speed, world.getTime());
    }

    private static void maybePlayGloatLaughSound(AllayEntity allay, ServerWorld world) {
        long now = world.getTime();
        long gloatUntil = getGloatUntil(allay);
        if (gloatUntil < 0L || now > gloatUntil) return;
        if (wasGloatLaughPlayedForWindow(allay, gloatUntil)) return;
        float pitch = 1.05f + (world.random.nextFloat() * 0.20f);
        float volume = 0.16f + (world.random.nextFloat() * 0.07f);
        world.playSound(
            null,
            allay.getX(),
            allay.getY(),
            allay.getZ(),
            ModSounds.NUISANCE_ALLAY_LAUGH,
            SoundCategory.NEUTRAL,
            volume,
            pitch
        );
        markGloatLaughPlayedForWindow(allay, gloatUntil);
        // #region agent log
        debugLog(
            allay,
            "H23",
            "NuisanceAllayHelper#maybePlayGloatLaughSound",
            "Gloat laugh audio played",
            "{\"event\":\"essencelib:nuisance.allay_laugh\",\"entityId\":" + allay.getId() + ",\"now\":" + now + ",\"gloatUntil\":" + gloatUntil + "}"
        );
        // #endregion
    }

    private static boolean isPlayerClosingDistance(AllayEntity allay, PlayerEntity player) {
        Vec3d toAllay = flattenHorizontal(allay.getPos().subtract(player.getPos()));
        if (toAllay.lengthSquared() < 0.0001) return true;
        Vec3d towardAllay = toAllay.normalize();
        Vec3d playerVel = flattenHorizontal(player.getVelocity());
        if (playerVel.lengthSquared() < 0.010) return false;
        return playerVel.normalize().dotProduct(towardAllay) > 0.22;
    }

    private static void moveHiddenDrift(AllayEntity allay, ServerWorld world, double speed) {
        long now = world.getTime();
        // Use time-based sine waves for smooth, continuous drifting instead of random jumps
        double dx = Math.sin(now * 0.05 + allay.getId()) * 2.0;
        double dz = Math.cos(now * 0.04 + allay.getId()) * 2.0;
        double dy = Math.sin(now * 0.02 + allay.getId()) * 0.5;
        
        Vec3d target = allay.getPos().add(dx, dy, dz);
        moveTo(allay, clampToWorldHeight(world, target), speed, now);
    }

    private static int indexOfMostValuableStealableSlot(ChestBlockEntity chest) {
        int bestSlot = -1;
        int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < chest.size(); i++) {
            ItemStack stack = chest.getStack(i);
            if (!isStealableItem(stack)) continue;
            int score = getItemValueScore(stack);
            if (score > bestScore) {
                bestScore = score;
                bestSlot = i;
            }
        }
        return bestSlot;
    }

    private static int getItemValueScore(ItemStack stack) {
        int rarityScore = switch (stack.getRarity()) {
            case COMMON -> 0;
            case UNCOMMON -> 3;
            case RARE -> 7;
            case EPIC -> 11;
        };
        int enchantScore = stack.hasEnchantments() ? 5 : 0;
        int treasureScore = 0;
        if (stack.isOf(Items.DIAMOND) || stack.isOf(Items.DIAMOND_BLOCK)) treasureScore += 6;
        if (stack.isOf(Items.EMERALD)) treasureScore += 4;
        if (stack.isOf(Items.NETHERITE_INGOT)) treasureScore += 8;
        return rarityScore + enchantScore + treasureScore;
    }

    private static boolean isStealableItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return !(stack.getItem() instanceof MobEssenceTrinketItem);
    }

    private static int getStealAmount(ItemStack stack) {
        if (stack.isEmpty()) return 1;
        return Math.max(1, Math.min(stack.getCount(), Math.min(stack.getMaxCount(), 64)));
    }

    private static boolean hasImmediateStealTarget(AllayEntity allay, ServerWorld world) {
        Box closeBox = allay.getBoundingBox().expand(IMMEDIATE_GRAB_RANGE);
        return !world.getEntitiesByClass(
            ItemEntity.class,
            closeBox,
            item -> item.isAlive() && !item.cannotPickup() && isStealableItem(item.getStack())
        ).isEmpty();
    }

    private static boolean hasCloseStealTarget(AllayEntity allay, ServerWorld world, double range) {
        Box closeBox = allay.getBoundingBox().expand(range);
        return !world.getEntitiesByClass(
            ItemEntity.class,
            closeBox,
            item -> item.isAlive() && !item.cannotPickup() && isStealableItem(item.getStack())
        ).isEmpty();
    }

    public static boolean isSeenByPlayer(PlayerEntity player, AllayEntity allay) {
        double lookDot = getPlayerLookDotToAllay(player, allay);
        boolean canSee = player.canSee(allay);
        // If the line of sight is blocked by world geometry/leaves, treat as unseen.
        if (!canSee) {
            return false;
        }
        if (lookDot >= STALK_SEEN_DOT) {
            return true;
        }
        // Treat "player turning toward allay" as seen to force prompt hide.
        if (lookDot >= STALK_TURNING_TO_SEE_DOT) {
            return true;
        }
        // Pre-emptive hide: if the player is actively rotating and the allay is near the forward hemisphere,
        // assume they'll spot it within a moment and hide early.
        if (isPlayerLikelyTurningTowardAllay(player, allay, lookDot)) {
            return true;
        }
        return false;
    }

    private static boolean isPlayerLikelyTurningTowardAllay(PlayerEntity player, AllayEntity allay, double lookDot) {
        if (lookDot >= PRETURN_FRONT_HEMISPHERE_DOT) return false;
        float yawDelta = Math.abs(MathHelper.wrapDegrees(player.getYaw() - player.prevYaw));
        if (yawDelta < PRETURN_YAW_THRESHOLD_DEG) return false;

        Vec3d lookNow = flattenHorizontal(player.getRotationVec(1.0f));
        if (lookNow.lengthSquared() < 0.0001) return false;
        lookNow = lookNow.normalize();

        Vec3d toAllay = flattenHorizontal(allay.getPos().subtract(player.getEyePos()));
        if (toAllay.lengthSquared() < 0.0001) return true;

        double facingDot = lookNow.dotProduct(toAllay.normalize());
        return facingDot >= PRETURN_FRONT_HEMISPHERE_DOT;
    }

    public static boolean isPlayerChasing(AllayEntity allay, PlayerEntity player, long now) {
        double distSq = allay.squaredDistanceTo(player);
        Vec3d toAllay = flattenHorizontal(allay.getPos().subtract(player.getPos()));
        if (toAllay.lengthSquared() < 0.0001) {
            return true;
        }
        Vec3d towardAllay = toAllay.normalize();
        Vec3d playerVel = flattenHorizontal(player.getVelocity());
        Vec3d playerLook = flattenHorizontal(player.getRotationVec(1.0f));
        if (playerLook.lengthSquared() > 0.0001) {
            playerLook = playerLook.normalize();
        }

        // If the player is moving towards the Allay, they are chasing
        boolean movingToward = playerVel.lengthSquared() > 0.010 && playerVel.normalize().dotProduct(towardAllay) > 0.18;
        
        // If the player is looking at the Allay AND is very close (within 7 blocks), consider it chasing even if not moving
        boolean lookingToward = playerLook.lengthSquared() > 0.0001 && playerLook.dotProduct(towardAllay) > 0.34;
        boolean closeAndAggressive = distSq < (7.0 * 7.0) && (movingToward || lookingToward);
        
        if (closeAndAggressive) {
            setChaseUntil(allay, now + CHASE_MEMORY_TICKS);
            return true;
        }
        long chaseUntil = getChaseUntil(allay);
        if (chaseUntil >= now) {
            return true;
        }
        clearChaseUntilTag(allay);
        return false;
    }

    private static void maybeExpireStolenItem(AllayEntity allay, ServerWorld world) {
        long stolenAt = getStolenTimestamp(allay);
        if (stolenAt < 0L) return;
        if (world.getTime() - stolenAt < STOLEN_ITEM_TIMEOUT_TICKS) return;
        allay.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, allay.getX(), allay.getY() + 0.22, allay.getZ(), 10, 0.12, 0.08, 0.12, 0.0);
        world.spawnParticles(ParticleTypes.SOUL, allay.getX(), allay.getY() + 0.22, allay.getZ(), 8, 0.10, 0.06, 0.10, 0.0);
        clearHoldingStateTags(allay);
        // When loot times out, perform a vertical breakaway reset.
        setClimbUntil(allay, world.getTime() + CLIMB_TICKS);
        setPanicUntil(allay, world.getTime() + CLIMB_TICKS + PANIC_SPRINT_TICKS);
    }

    private static void clearHoldingStateTags(AllayEntity allay) {
        clearStolenTimestampTag(allay);
        clearStolenPlayerTag(allay);
        clearGloatUntilTag(allay);
        clearOrbitUntilTag(allay);
        clearRestUntilTag(allay);
        clearChaseUntilTag(allay);
        allay.removeCommandTag(PLAYER_LOOT_STEAL_TAG);
        allay.removeCommandTag(CHEST_WAIT_GLOAT_TAG);
    }

    private static void clearAllStateTags(AllayEntity allay) {
        clearHoldingStateTags(allay);
        clearPanicUntilTag(allay);
        clearClimbUntilTag(allay);
    }

    private static void setStolenTimestamp(AllayEntity allay, long gameTime, int itemAge) {
        clearStolenTimestampTag(allay);
        // Use actual steal time so getaway/gloat windows start at pickup moment.
        allay.addCommandTag(STOLEN_AT_TAG_PREFIX + gameTime);
    }

    private static void setStolenPlayer(AllayEntity allay, PlayerEntity player) {
        clearStolenPlayerTag(allay);
        if (player == null) return;
        allay.addCommandTag(STOLEN_PLAYER_TAG_PREFIX + player.getId());
    }

    private static PlayerEntity getStolenPlayer(ServerWorld world, AllayEntity allay) {
        int id = -1;
        for (String tag : allay.getCommandTags()) {
            if (!tag.startsWith(STOLEN_PLAYER_TAG_PREFIX)) continue;
            try {
                id = Integer.parseInt(tag.substring(STOLEN_PLAYER_TAG_PREFIX.length()));
            } catch (NumberFormatException ignored) {
                return null;
            }
            break;
        }
        if (id < 0) return null;
        if (world.getEntityById(id) instanceof PlayerEntity player && !player.isSpectator()) {
            return player;
        }
        return null;
    }

    private static long getStolenTimestamp(AllayEntity allay) {
        return getTimestampFromTags(allay, STOLEN_AT_TAG_PREFIX);
    }

    private static void clearStolenTimestampTag(AllayEntity allay) {
        clearTagsByPrefix(allay, STOLEN_AT_TAG_PREFIX);
    }

    private static void clearStolenPlayerTag(AllayEntity allay) {
        clearTagsByPrefix(allay, STOLEN_PLAYER_TAG_PREFIX);
    }

    private static void setClimbUntil(AllayEntity allay, long gameTime) {
        clearClimbUntilTag(allay);
        allay.addCommandTag(CLIMB_UNTIL_TAG_PREFIX + gameTime);
    }

    private static long getClimbUntil(AllayEntity allay) {
        return getTimestampFromTags(allay, CLIMB_UNTIL_TAG_PREFIX);
    }

    private static boolean isClimbActive(AllayEntity allay, long now) {
        long climbUntil = getClimbUntil(allay);
        if (climbUntil < 0L) return false;
        if (climbUntil >= now) return true;
        clearClimbUntilTag(allay);
        return false;
    }

    private static void clearClimbUntilTag(AllayEntity allay) {
        clearTagsByPrefix(allay, CLIMB_UNTIL_TAG_PREFIX);
    }

    private static void setPanicUntil(AllayEntity allay, long gameTime) {
        clearPanicUntilTag(allay);
        allay.addCommandTag(PANIC_UNTIL_TAG_PREFIX + gameTime);
    }

    private static long getPanicUntil(AllayEntity allay) {
        return getTimestampFromTags(allay, PANIC_UNTIL_TAG_PREFIX);
    }

    private static boolean isPanicActive(AllayEntity allay, long now) {
        long panicUntil = getPanicUntil(allay);
        if (panicUntil < 0L) return false;
        if (panicUntil >= now) return true;
        clearPanicUntilTag(allay);
        return false;
    }

    private static void clearPanicUntilTag(AllayEntity allay) {
        clearTagsByPrefix(allay, PANIC_UNTIL_TAG_PREFIX);
    }

    private static void setRestUntil(AllayEntity allay, long gameTime) {
        clearRestUntilTag(allay);
        allay.addCommandTag(REST_UNTIL_TAG_PREFIX + gameTime);
    }

    private static long getRestUntil(AllayEntity allay) {
        return getTimestampFromTags(allay, REST_UNTIL_TAG_PREFIX);
    }

    private static boolean isRestActive(AllayEntity allay, long now) {
        long restUntil = getRestUntil(allay);
        if (restUntil < 0L) return false;
        if (now <= restUntil) return true;
        clearRestUntilTag(allay);
        return false;
    }

    private static void clearRestUntilTag(AllayEntity allay) {
        clearTagsByPrefix(allay, REST_UNTIL_TAG_PREFIX);
    }

    private static void setGloatUntil(AllayEntity allay, long gameTime) {
        clearGloatUntilTag(allay);
        allay.addCommandTag(GLOAT_UNTIL_TAG_PREFIX + gameTime);
        // #region agent log
        debugLog(
            allay,
            "H25",
            "NuisanceAllayHelper#setGloatUntil",
            "Opened gloat window",
            "{\"entityId\":" + allay.getId() + ",\"gloatUntil\":" + gameTime + "}"
        );
        // #endregion
    }

    private static long getGloatUntil(AllayEntity allay) {
        return getTimestampFromTags(allay, GLOAT_UNTIL_TAG_PREFIX);
    }

    private static void clearGloatUntilTag(AllayEntity allay) {
        clearTagsByPrefix(allay, GLOAT_UNTIL_TAG_PREFIX);
        clearTagsByPrefix(allay, GLOAT_LAUGH_PLAYED_TAG_PREFIX);
    }

    private static boolean wasGloatLaughPlayedForWindow(AllayEntity allay, long gloatUntil) {
        for (String tag : allay.getCommandTags()) {
            if (!tag.startsWith(GLOAT_LAUGH_PLAYED_TAG_PREFIX)) continue;
            try {
                long playedFor = Long.parseLong(tag.substring(GLOAT_LAUGH_PLAYED_TAG_PREFIX.length()));
                if (playedFor == gloatUntil) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    private static void markGloatLaughPlayedForWindow(AllayEntity allay, long gloatUntil) {
        clearTagsByPrefix(allay, GLOAT_LAUGH_PLAYED_TAG_PREFIX);
        allay.addCommandTag(GLOAT_LAUGH_PLAYED_TAG_PREFIX + gloatUntil);
    }

    private static void setLastGloatEnd(AllayEntity allay, long gameTime) {
        clearTagsByPrefix(allay, LAST_GLOAT_END_TAG_PREFIX);
        allay.addCommandTag(LAST_GLOAT_END_TAG_PREFIX + gameTime);
    }

    private static long getLastGloatEnd(AllayEntity allay) {
        return getTimestampFromTags(allay, LAST_GLOAT_END_TAG_PREFIX);
    }

    private static void setOrbitUntil(AllayEntity allay, long gameTime) {
        clearOrbitUntilTag(allay);
        allay.addCommandTag(ORBIT_UNTIL_TAG_PREFIX + gameTime);
    }

    private static long getOrbitUntil(AllayEntity allay) {
        return getTimestampFromTags(allay, ORBIT_UNTIL_TAG_PREFIX);
    }

    private static boolean isOrbitActive(AllayEntity allay, long now) {
        long orbitUntil = getOrbitUntil(allay);
        if (orbitUntil < 0L) return false;
        if (orbitUntil >= now) return true;
        clearOrbitUntilTag(allay);
        return false;
    }

    private static void clearOrbitUntilTag(AllayEntity allay) {
        clearTagsByPrefix(allay, ORBIT_UNTIL_TAG_PREFIX);
    }

    private static void setChaseUntil(AllayEntity allay, long gameTime) {
        clearChaseUntilTag(allay);
        allay.addCommandTag(CHASE_UNTIL_TAG_PREFIX + gameTime);
    }

    private static long getChaseUntil(AllayEntity allay) {
        return getTimestampFromTags(allay, CHASE_UNTIL_TAG_PREFIX);
    }

    private static void clearChaseUntilTag(AllayEntity allay) {
        clearTagsByPrefix(allay, CHASE_UNTIL_TAG_PREFIX);
    }

    private static long getTimestampFromTags(AllayEntity allay, String prefix) {
        for (String tag : allay.getCommandTags()) {
            if (!tag.startsWith(prefix)) continue;
            try {
                return Long.parseLong(tag.substring(prefix.length()));
            } catch (NumberFormatException ignored) {
                return -1L;
            }
        }
        return -1L;
    }

    private static void clearTagsByPrefix(AllayEntity allay, String prefix) {
        for (String tag : List.copyOf(allay.getCommandTags())) {
            if (tag.startsWith(prefix)) {
                allay.removeCommandTag(tag);
            }
        }
    }

    private static String escapeJson(String value) {
        if (value == null) return "";
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    private static String getDebugState(AllayEntity allay) {
        for (String tag : allay.getCommandTags()) {
            if (tag.startsWith(DEBUG_STATE_TAG_PREFIX)) {
                return tag.substring(DEBUG_STATE_TAG_PREFIX.length());
            }
        }
        return "idle";
    }

    private static void emitDebugHeadEffect(AllayEntity allay, ServerWorld world, PlayerEntity focusPlayer, long now) {
        if (!DEBUG_HEAD_EFFECT_ENABLED) return;
        if ((now + allay.getId()) % DEBUG_HEAD_EFFECT_INTERVAL_TICKS != 0L) return;

        String state = getDebugState(allay);
        allay.setCustomName(Text.literal("[" + state.toUpperCase() + "]").formatted(Formatting.AQUA, Formatting.BOLD));
        allay.setCustomNameVisible(true);

        switch (state) {
            case "steal" -> world.spawnParticles(ParticleTypes.CRIT, allay.getX(), allay.getY() + 0.85, allay.getZ(), 2, 0.12, 0.08, 0.12, 0.0);
            case "swoop_wait" -> world.spawnParticles(ParticleTypes.SMOKE, allay.getX(), allay.getY() + 0.85, allay.getZ(), 2, 0.10, 0.08, 0.10, 0.0);
            case "flee" -> world.spawnParticles(ParticleTypes.CLOUD, allay.getX(), allay.getY() + 0.85, allay.getZ(), 2, 0.14, 0.10, 0.14, 0.0);
            case "hide", "stalk_cover" -> world.spawnParticles(ParticleTypes.SMOKE, allay.getX(), allay.getY() + 0.85, allay.getZ(), 2, 0.10, 0.08, 0.10, 0.0);
            case "stalk", "reengage" -> world.spawnParticles(ParticleTypes.END_ROD, allay.getX(), allay.getY() + 0.85, allay.getZ(), 1, 0.08, 0.06, 0.08, 0.0);
            case "taunt", "orbit" -> world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, allay.getX(), allay.getY() + 0.85, allay.getZ(), 2, 0.10, 0.08, 0.10, 0.0);
            case "acquire" -> world.spawnParticles(ParticleTypes.ENCHANT, allay.getX(), allay.getY() + 0.85, allay.getZ(), 2, 0.10, 0.08, 0.10, 0.0);
            default -> world.spawnParticles(ParticleTypes.WITCH, allay.getX(), allay.getY() + 0.85, allay.getZ(), 1, 0.08, 0.06, 0.08, 0.0);
        }
    }

    private static void moveTo(AllayEntity allay, Vec3d target, double speed, long now) {
        target = clampToWorldHeight(allay.getWorld(), target);
        String state = getDebugState(allay);
        boolean allowRamp = "stalk".equals(state) || "stalk_cover".equals(state) || "swoop_wait".equals(state);
        double moveSpeed = speed;
        if (allowRamp) {
            double dist = allay.getPos().distanceTo(target);
            double ramp = Math.max(0.52, Math.min(1.0, dist / 4.8));
            moveSpeed = speed * ramp;
        }
        
        // Force direct movement via MoveControl (bypasses Brain and pathfinder)
        allay.getMoveControl().moveTo(target.x, target.y, target.z, moveSpeed);
        allay.getLookControl().lookAt(target.x, target.y, target.z, 30.0f, 30.0f);
    }

    private static Vec3d clampStealHeight(Vec3d target, ItemEntity item) {
        double weightedY = item.getY() + 0.15;
        double y = Math.max(weightedY - 0.15, Math.min(weightedY + 0.35, target.y));
        return new Vec3d(target.x, y, target.z);
    }

    private static Vec3d clampTargetHeight(Vec3d target, PlayerEntity player, double minOffset, double maxOffset) {
        double minY = player.getY() + minOffset;
        double maxY = player.getY() + maxOffset;
        double y = Math.max(minY, Math.min(maxY, target.y));
        return new Vec3d(target.x, y, target.z);
    }

    private static Vec3d clampToWorldHeight(World world, Vec3d target) {
        double minY = world.getBottomY() + 0.1;
        double maxY = world.getTopY() - 0.1;
        double y = Math.max(minY, Math.min(maxY, target.y));
        return new Vec3d(target.x, y, target.z);
    }

    private static Vec3d flattenHorizontal(Vec3d vec) {
        return new Vec3d(vec.x, 0.0, vec.z);
    }

    private static Vec3d getRightVector(Vec3d look) {
        Vec3d right = look.crossProduct(new Vec3d(0.0, 1.0, 0.0));
        if (right.lengthSquared() < 0.0001) {
            return new Vec3d(1.0, 0.0, 0.0);
        }
        return right.normalize();
    }

    private static double getPlayerLookDotToAllay(PlayerEntity player, AllayEntity allay) {
        Vec3d look = player.getRotationVec(1.0f).normalize();
        Vec3d toAllay = allay.getPos().subtract(player.getEyePos());
        if (toAllay.lengthSquared() < 0.0001) {
            return 1.0;
        }
        return look.dotProduct(toAllay.normalize());
    }

    public static void notifyChestOpened(ServerWorld world, BlockPos pos) {
        long now = world.getTime();
        long delay = CHEST_STEAL_DELAY_MIN_TICKS + world.random.nextInt((int) (CHEST_STEAL_DELAY_MAX_TICKS - CHEST_STEAL_DELAY_MIN_TICKS + 1));
        OPENED_CHEST_WINDOWS.put(
            new ChestWindowKey(world.getRegistryKey(), pos.toImmutable()),
            new ChestOpenWindow(now + delay, now + CHEST_STEAL_WINDOW_TICKS)
        );
        // #region agent log
        debugLogGlobal(
            "H32",
            "NuisanceAllayHelper#notifyChestOpened",
            "Chest open window registered",
            "{\"pos\":\"" + pos.toShortString() + "\",\"openAfter\":" + (now + delay) + ",\"expire\":" + (now + CHEST_STEAL_WINDOW_TICKS) + "}"
        );
        // #endregion
    }

    private static boolean hasOpenChestWindowNearby(ServerWorld world, AllayEntity allay, double range) {
        double rangeSq = range * range;
        BlockPos center = allay.getBlockPos();
        for (Map.Entry<ChestWindowKey, ChestOpenWindow> entry : OPENED_CHEST_WINDOWS.entrySet()) {
            if (entry.getKey().dimension != world.getRegistryKey()) continue;
            if (entry.getKey().pos.getSquaredDistance(center) > rangeSq) continue;
            ChestOpenWindow window = entry.getValue();
            if (window == null) continue;
            long now = world.getTime();
            if (now >= window.openAfterTick && now <= window.expireTick) {
                return true;
            }
        }
        return false;
    }

    private static boolean isChestOpenWindowActive(ServerWorld world, BlockPos pos) {
        ChestOpenWindow window = OPENED_CHEST_WINDOWS.get(new ChestWindowKey(world.getRegistryKey(), pos));
        if (window == null) return false;
        long now = world.getTime();
        return now >= window.openAfterTick && now <= window.expireTick;
    }

    private static void pruneExpiredChestWindows(ServerWorld world) {
        long now = world.getTime();
        OPENED_CHEST_WINDOWS.entrySet().removeIf(e -> {
            ChestWindowKey key = e.getKey();
            ChestOpenWindow window = e.getValue();
            return key.dimension != world.getRegistryKey() || window == null || window.expireTick < now;
        });
    }

    private static boolean isAnyNearbyPlayerInContainer(ServerWorld world, AllayEntity allay, double range) {
        double rangeSq = range * range;
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.squaredDistanceTo(allay) > rangeSq) continue;
            if (player.currentScreenHandler != player.playerScreenHandler) {
                return true;
            }
        }
        return false;
    }

    private record ChestWindowKey(RegistryKey<World> dimension, BlockPos pos) {}

    private record ChestOpenWindow(long openAfterTick, long expireTick) {}
}
