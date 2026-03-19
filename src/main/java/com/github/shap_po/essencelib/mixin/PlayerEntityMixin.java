package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.component.DownedComponent;
import com.github.shap_po.essencelib.component.RestrainedComponent;
import com.github.shap_po.essencelib.guillotine.RestrainedHelper;
import com.github.shap_po.essencelib.power.type.ModifyPlayerModelPowerType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import com.github.shap_po.essencelib.combat.AggroAnalysisHelper;
import com.github.shap_po.essencelib.config.ServerAbilityHookConfig;
import com.github.shap_po.essencelib.hud.PowerHudSyncRegistry;
import com.github.shap_po.essencelib.networking.ManaPackets;
import com.github.shap_po.essencelib.networking.PowerHudPackets;
import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import com.github.shap_po.essencelib.util.DownedStateHelper;
import com.github.shap_po.essencelib.util.EssenceCleanupHelper;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerManager;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.PowerUtil;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    private float lastCurrentMana = -1;
    private float lastMaxMana = -1;
    @Unique private String essencelib$lastHudSnapshot = null;
    @Unique private UUID essencelib$lastThreatMobUuid;
    @Unique private double essencelib$lastThreatDistanceSq = -1.0;
    @Unique private int essencelib$threatTrackTicks = 0;
    @Unique private double essencelib$lastPosX;
    @Unique private double essencelib$lastPosZ;
    @Unique private boolean essencelib$hasLastPos = false;
    @Unique private int essencelib$frontlineGainTicks = 0;
    @Unique private int essencelib$frontlineLossTicks = 0;
    @Unique private int essencelib$retreatDrainTicks = 0;
    @Unique private boolean essencelib$initializedTransientStates = false;
    @Unique private int essencelib$downedMessageCooldown = 0;
    @Unique private boolean essencelib$armadilloForcedSneak = false;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        // Initialize and get attributes in one go
        var maxMana = initializeAttribute(player, ManaAttributeRegistry.getMaxManaEntry());
        var currentMana = initializeAttribute(player, ManaAttributeRegistry.getCurrentManaEntry());
        var manaRegen = initializeAttribute(player, ManaAttributeRegistry.getManaRegenEntry());

        if (maxMana != null && currentMana != null && manaRegen != null) {
            // Set initial values
            if (maxMana.getBaseValue() <= 0) maxMana.setBaseValue(100);
            if (currentMana.getBaseValue() <= 0) currentMana.setBaseValue(0);
            // Slow natural base regen (~2 mana/min) so users don't waste mana or hunt for regen items
            if (manaRegen.getBaseValue() <= 0) manaRegen.setBaseValue(0.4);

            // Send initial values to client
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ManaPackets.sendManaUpdate(serverPlayer, (float) currentMana.getValue(), (float) maxMana.getValue());
            }
        }
    }

    private EntityAttributeInstance initializeAttribute(PlayerEntity player, RegistryEntry<EntityAttribute> entry) {
        if (!player.getAttributes().hasAttribute(entry)) {
            return player.getAttributeInstance(entry);
        }
        return player.getAttributeInstance(entry);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (DownedComponent.isDowned(player)) {
            player.setPose(EntityPose.DYING);
            player.setSprinting(false);
            player.setSneaking(false);
            player.setVelocity(0, player.getVelocity().y, 0);
            player.velocityModified = true;
            if (!player.getWorld().isClient) {
                // Keep mobs disengaged while the player remains downed.
                DownedStateHelper.clearMobAggroAround(player, 64.0D);
                if (essencelib$downedMessageCooldown <= 0) {
                    DownedStateHelper.sendDownedActionbar(player);
                    essencelib$downedMessageCooldown = 40;
                } else {
                    essencelib$downedMessageCooldown--;
                }
            }
            return;
        }
        essencelib$downedMessageCooldown = 0;
        // Guillotine restraint: hold player in place, no sneaking or moving out
        if (!player.getWorld().isClient && RestrainedComponent.isRestrained(player)) {
            RestrainedComponent comp = RestrainedComponent.getNullable(player);
            if (comp != null && comp.getAnchorPos() != null && comp.getDimension() != null
                && player.getWorld().getRegistryKey().equals(comp.getDimension())) {
                BlockPos anchor = comp.getAnchorPos();
                RestrainedHelper.snapToGuillotinePose(player, anchor);
            }
        }
        if (isArmadilloBallUpActive(player)) {
            // Force crouch pose in code while Ball Up is active.
            player.setSneaking(true);
            player.setPose(EntityPose.CROUCHING);
            essencelib$armadilloForcedSneak = true;
        } else if (essencelib$armadilloForcedSneak) {
            // Release forced crouch once Ball Up ends so player state does not get stuck.
            player.setSneaking(false);
            essencelib$armadilloForcedSneak = false;
        }

        if (!player.getWorld().isClient) {
            if (!essencelib$initializedTransientStates) {
                // Prevent persisted transient state from auto-activating Ball Up after relog.
                essencelib$initializedTransientStates = EssenceCleanupHelper.resetArmadilloBallStateIfPresent(player);
            }
            essencelib$tickThreatRhythmRunAwayPenalty(player);
        }

        if (player instanceof ServerPlayerEntity serverPlayer) {
            float current = (float) player.getAttributeValue(ManaAttributeRegistry.getCurrentManaEntry());
            float max = (float) player.getAttributeValue(ManaAttributeRegistry.getMaxManaEntry());

            // Only send update if values changed
            if (current != lastCurrentMana || max != lastMaxMana) {
                ManaPackets.sendManaUpdate(serverPlayer, current, max);
                lastCurrentMana = current;
                lastMaxMana = max;
            }

            Map<Identifier, Integer> hudValues = essencelib$collectHudResources(player);
            String snapshot = essencelib$buildHudSnapshot(hudValues);
            if (!snapshot.equals(essencelib$lastHudSnapshot)) {
                PowerHudPackets.send(serverPlayer, hudValues);
                essencelib$lastHudSnapshot = snapshot;
            }
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void essencelib$trackLastThreatMob(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) return;
        if (!(source.getAttacker() instanceof MobEntity mob)) return;
        if (mob.isRemoved() || !mob.isAlive()) return;

        essencelib$lastThreatMobUuid = mob.getUuid();
        essencelib$lastThreatDistanceSq = player.squaredDistanceTo(mob);
        essencelib$threatTrackTicks = ServerAbilityHookConfig.get().threatTrackTicks();
        essencelib$applyThreatDamageResolveRule(player, mob);
    }

    private static boolean isArmadilloBallUpActive(PlayerEntity player) {
        return PowerHolderComponent.KEY.get(player).getPowerTypes(ModifyPlayerModelPowerType.class).stream()
            .anyMatch(p -> p.getModel().equals(EssenceLib.identifier("armadillo_ball")) && p.isActive());
    }

    @Unique
    private void essencelib$tickThreatRhythmRunAwayPenalty(PlayerEntity player) {
        ServerAbilityHookConfig.Config cfg = ServerAbilityHookConfig.get();
        double currentX = player.getX();
        double currentZ = player.getZ();
        if (!essencelib$hasLastPos) {
            essencelib$lastPosX = currentX;
            essencelib$lastPosZ = currentZ;
            essencelib$hasLastPos = true;
            return;
        }

        double moveX = currentX - essencelib$lastPosX;
        double moveZ = currentZ - essencelib$lastPosZ;
        essencelib$lastPosX = currentX;
        essencelib$lastPosZ = currentZ;

        if (essencelib$threatTrackTicks <= 0 || essencelib$lastThreatMobUuid == null) return;
        essencelib$threatTrackTicks--;

        if (!(player.getWorld() instanceof ServerWorld serverWorld)) return;
        var tracked = serverWorld.getEntity(essencelib$lastThreatMobUuid);
        if (!(tracked instanceof MobEntity mob) || !mob.isAlive()) {
            essencelib$lastThreatMobUuid = null;
            essencelib$lastThreatDistanceSq = -1.0;
            essencelib$threatTrackTicks = 0;
            return;
        }

        // Only punish retreat while this mob is still actively focused on the player.
        if (!AggroAnalysisHelper.isMobAggroingPlayer(mob, player)) {
            essencelib$lastThreatDistanceSq = player.squaredDistanceTo(mob);
            essencelib$frontlineGainTicks = 0;
            essencelib$frontlineLossTicks = 0;
            essencelib$retreatDrainTicks = 0;
            return;
        }

        // Mob is actively chasing/targeting the player; mark chased state.
        essencelib$changeThreatResource(player, cfg.threatRhythmChasedStatePower(), 8);

        double currentDistSq = player.squaredDistanceTo(mob);
        AggroAnalysisHelper.Analysis analysis = AggroAnalysisHelper.analyze(player, mob, moveX, moveZ, cfg.minPlayerMoveSq(), cfg.facingTargetDotMin());
        boolean movingAwayFromMob = analysis.movingAway();
        boolean facingTarget = analysis.facingTarget();
        boolean facingAwayFromMob = analysis.facingAway();
        double lookDotTarget = analysis.lookDotTarget();

        // Reward frontline posture: keep aggro and face the target instead of kiting.
        if (facingTarget && !movingAwayFromMob) {
            essencelib$frontlineGainTicks++;
            essencelib$frontlineLossTicks = 0;
            if (essencelib$frontlineGainTicks >= cfg.frontlineGainIntervalTicks()) {
                essencelib$frontlineGainTicks = 0;
                essencelib$changeThreatResource(player, cfg.threatRhythmResolvePower(), 3);
                essencelib$changeThreatResource(player, cfg.threatRhythmAggroTimerPower(), 4);
            }
        } else {
            essencelib$frontlineGainTicks = 0;
            essencelib$frontlineLossTicks++;
            if (!movingAwayFromMob && essencelib$frontlineLossTicks >= cfg.frontlineLossIntervalTicks()) {
                essencelib$frontlineLossTicks = 0;
                // You have aggro but aren't actively holding it (not facing target): lose resolve.
                essencelib$changeThreatResource(player, cfg.threatRhythmResolvePower(), -2);
            }
        }

        boolean hardKiteIntent = movingAwayFromMob && (player.isSprinting() || lookDotTarget <= cfg.hardKiteFacingDotMax());
        if (hardKiteIntent) {
            Power resolvePower = PowerManager.getNullable(cfg.threatRhythmResolvePower());
            if (resolvePower != null) {
                PowerType resolveType = PowerUtil.getNullablePowerType(resolvePower, player);
                if (resolveType != null && PowerUtil.getResourceValue(resolveType) > 0) {
                    PowerUtil.setResourceValue(resolveType, 0);
                }
            }
            essencelib$retreatDrainTicks = 0;
            essencelib$lastThreatDistanceSq = currentDistSq;
            return;
        }

        // Soft retreat drain: if you're still opening distance while chased, drain steadily.
        if (movingAwayFromMob && essencelib$lastThreatDistanceSq >= 0
            && currentDistSq > essencelib$lastThreatDistanceSq + cfg.awayDistanceSqStep()) {
            essencelib$retreatDrainTicks++;
            if (essencelib$retreatDrainTicks >= cfg.retreatDrainIntervalTicks()) {
                essencelib$retreatDrainTicks = 0;
                essencelib$changeThreatResource(player, cfg.threatRhythmResolvePower(), -cfg.retreatDrainPerStep());
            }
        } else if (movingAwayFromMob && (facingAwayFromMob || player.isSprinting())) {
            // Preserve old harsher fallback when behavior still implies kiting intent.
            essencelib$changeThreatResource(player, cfg.threatRhythmResolvePower(), -cfg.awayPenaltyPerStep());
            essencelib$retreatDrainTicks = 0;
        } else {
            essencelib$retreatDrainTicks = 0;
        }

        essencelib$lastThreatDistanceSq = currentDistSq;
    }

    @Unique
    private void essencelib$changeThreatResource(PlayerEntity player, Identifier powerId, int delta) {
        if (delta == 0) return;
        Power power = PowerManager.getNullable(powerId);
        if (power == null) return;
        PowerType powerType = PowerUtil.getNullablePowerType(power, player);
        if (powerType == null) return;
        int current = PowerUtil.getResourceValue(powerType);
        int target = current + delta;
        if (delta < 0 && current <= 0) return;
        PowerUtil.setResourceValue(powerType, Math.max(0, target));
    }

    @Unique
    private int essencelib$getResourceValue(PlayerEntity player, Identifier powerId) {
        Power power = PowerManager.getNullable(powerId);
        if (power == null) return 0;
        PowerType powerType = PowerUtil.getNullablePowerType(power, player);
        if (powerType == null) return 0;
        return Math.max(0, PowerUtil.getResourceValue(powerType));
    }

    @Unique
    private void essencelib$applyThreatDamageResolveRule(PlayerEntity player, MobEntity mob) {
        ServerAbilityHookConfig.Config cfg = ServerAbilityHookConfig.get();
        if (!essencelib$hasThreatPower(player, cfg.threatRhythmResolvePower())) return;
        if (!essencelib$hasThreatPower(player, cfg.threatRhythmAggroTimerPower())) return;

        double moveX = player.getX() - essencelib$lastPosX;
        double moveZ = player.getZ() - essencelib$lastPosZ;
        AggroAnalysisHelper.Analysis analysis = AggroAnalysisHelper.analyze(player, mob, moveX, moveZ, cfg.minPlayerMoveSq(), cfg.facingTargetDotMin());
        boolean movingAway = essencelib$hasLastPos && analysis.movingAway();
        boolean facingTarget = analysis.facingTarget();

        boolean retreatIntent = AggroAnalysisHelper.hasRetreatIntent(movingAway, player.isSprinting(), facingTarget);
        if (retreatIntent) {
            essencelib$changeThreatResource(player, cfg.threatRhythmResolvePower(), -cfg.damageRetreatResolvePenalty());
            return;
        }

        if (facingTarget && !movingAway) {
            essencelib$changeThreatResource(player, cfg.threatRhythmResolvePower(), cfg.damageFrontlineResolveGain());
        }
    }

    @Unique
    private boolean essencelib$hasThreatPower(PlayerEntity player, Identifier powerId) {
        Power power = PowerManager.getNullable(powerId);
        if (power == null) return false;
        return PowerUtil.getNullablePowerType(power, player) != null;
    }

    @Unique
    private Map<Identifier, Integer> essencelib$collectHudResources(PlayerEntity player) {
        Map<Identifier, Integer> values = new LinkedHashMap<>();
        for (Identifier powerId : PowerHudSyncRegistry.getTrackedPowerIds()) {
            Power power = PowerManager.getNullable(powerId);
            if (power == null) continue;
            PowerType powerType = PowerUtil.getNullablePowerType(power, player);
            if (powerType == null) continue; // only sync bars for powers this player currently has
            values.put(powerId, Math.max(0, PowerUtil.getResourceValue(powerType)));
        }
        return values;
    }

    @Unique
    private String essencelib$buildHudSnapshot(Map<Identifier, Integer> values) {
        if (values.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        for (var entry : values.entrySet()) {
            if (!out.isEmpty()) {
                out.append(';');
            }
            out.append(entry.getKey());
            out.append('=');
            out.append(entry.getValue());
        }
        return out.toString();
    }
}
