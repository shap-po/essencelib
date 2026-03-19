package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.EssenceLib;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry of reusable client detection components loaded from data.
 *
 * Profiles define:
 * - required power gate
 * - target type (item/mob/player/any)
 * - range formula (base + attr * coef, clamped)
 */
@Environment(EnvType.CLIENT)
public final class EntityDetectionComponentRegistry {

    private static final String CONFIG_PATH = "/data/essencelib/detection_components.json";
    private static Map<Identifier, DetectionProfile> profiles;

    public static boolean hasDetector(PlayerEntity player, Identifier profileId) {
        if (player == null || profileId == null) return false;
        DetectionProfile profile = getProfile(profileId);
        if (profile == null) return false;
        return profile.requiredPower() == null
            || EquippedEssencePowerHelper.hasVisibleEquippedPower(player, profile.requiredPower());
    }

    public static boolean canDetect(PlayerEntity player, Entity entity, Identifier profileId) {
        if (player == null || entity == null || profileId == null) return false;
        DetectionProfile profile = getProfile(profileId);
        if (profile == null) return false;
        if (!hasDetector(player, profileId)) return false;
        if (!matchesTarget(profile.targetType(), entity)) return false;
        double range = getRange(player, profileId);
        return player.squaredDistanceTo(entity) <= range * range;
    }

    public static boolean canDetectBlock(PlayerEntity player, BlockPos blockPos, Identifier profileId) {
        if (player == null || blockPos == null || profileId == null) return false;
        DetectionProfile profile = getProfile(profileId);
        if (profile == null) return false;
        if (!hasDetector(player, profileId)) return false;
        if (profile.targetType() != TargetType.BLOCK && profile.targetType() != TargetType.ANY) return false;
        double range = getRange(player, profileId);
        double cx = blockPos.getX() + 0.5;
        double cy = blockPos.getY() + 0.5;
        double cz = blockPos.getZ() + 0.5;
        return player.squaredDistanceTo(cx, cy, cz) <= range * range;
    }

    public static double getRange(PlayerEntity player, Identifier profileId) {
        DetectionProfile profile = getProfile(profileId);
        if (profile == null) return 0.0;
        double base = profile.baseRange();
        if (player == null) return base;

        double statValue = 0.0;
        if (profile.rangeAttribute() != null) {
            Optional<RegistryEntry.Reference<EntityAttribute>> attr = Registries.ATTRIBUTE.getEntry(profile.rangeAttribute());
            if (attr.isPresent()) {
                statValue = player.getAttributeValue(attr.get());
            }
        }
        double range = base + (statValue * profile.rangePerAttribute());
        double min = Math.min(profile.minRange(), profile.maxRange());
        double max = Math.max(profile.minRange(), profile.maxRange());
        return Math.max(min, Math.min(max, range));
    }

    public static DetectionProfile getProfile(Identifier profileId) {
        ensureLoaded();
        return profiles.get(profileId);
    }

    private static synchronized void ensureLoaded() {
        if (profiles != null) return;
        Map<Identifier, DetectionProfile> map = new HashMap<>();

        // Backward-compatible default profile from existing ability hooks keys.
        ClientAbilityHookConfig.Config cfg = ClientAbilityHookConfig.get();
        Identifier defaultProfileId = cfg.collectorIntuitionDetectionProfile();
        map.put(defaultProfileId, new DetectionProfile(
            cfg.collectorIntuitionPower(),
            TargetType.ITEM,
            cfg.collectionRangeAttribute(),
            cfg.collectorIntuitionBaseRange(),
            cfg.collectorIntuitionRangePerCollection(),
            cfg.collectorIntuitionMinRange(),
            cfg.collectorIntuitionMaxRange()
        ));

        try (var stream = EntityDetectionComponentRegistry.class.getResourceAsStream(CONFIG_PATH)) {
            if (stream != null) {
                JsonObject root = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
                if (root != null) {
                    JsonObject profilesObj = root.has("profiles") && root.get("profiles").isJsonObject()
                        ? root.getAsJsonObject("profiles")
                        : null;
                    if (profilesObj != null) {
                        for (var entry : profilesObj.entrySet()) {
                            Identifier id = Identifier.tryParse(entry.getKey());
                            if (id == null || !entry.getValue().isJsonObject()) continue;
                            DetectionProfile parsed = parseProfile(entry.getValue().getAsJsonObject());
                            if (parsed != null) map.put(id, parsed);
                        }
                    }
                }
            }
        } catch (Exception e) {
            EssenceLib.LOGGER.warn("Could not load detection component profiles: {}", e.getMessage());
        }

        profiles = Map.copyOf(map);
    }

    private static DetectionProfile parseProfile(JsonObject obj) {
        Identifier power = parseIdentifier(obj.get("required_power"), null);
        TargetType target = parseTargetType(obj.get("target_type"), TargetType.ANY);
        Identifier attr = parseIdentifier(obj.get("range_attribute"), null);
        double base = parseDouble(obj.get("base_range"), 0.0);
        double perAttr = parseDouble(obj.get("range_per_attribute"), 0.0);
        double min = parseDouble(obj.get("min_range"), 0.0);
        double max = parseDouble(obj.get("max_range"), Math.max(min, base));
        return new DetectionProfile(power, target, attr, base, perAttr, min, max);
    }

    private static boolean matchesTarget(TargetType type, Entity entity) {
        return switch (type) {
            case ANY -> true;
            case ITEM -> entity instanceof ItemEntity;
            case MOB -> entity instanceof MobEntity;
            case PLAYER -> entity instanceof PlayerEntity;
            case BLOCK -> false;
        };
    }

    private static TargetType parseTargetType(JsonElement element, TargetType def) {
        if (element == null || !element.isJsonPrimitive()) return def;
        String value = element.getAsString();
        return switch (value) {
            case "item" -> TargetType.ITEM;
            case "mob" -> TargetType.MOB;
            case "player" -> TargetType.PLAYER;
            case "any" -> TargetType.ANY;
            case "block" -> TargetType.BLOCK;
            default -> def;
        };
    }

    private static Identifier parseIdentifier(JsonElement element, Identifier def) {
        if (element == null || !element.isJsonPrimitive()) return def;
        Identifier parsed = Identifier.tryParse(element.getAsString());
        return parsed != null ? parsed : def;
    }

    private static double parseDouble(JsonElement element, double def) {
        if (element == null || !element.isJsonPrimitive()) return def;
        try {
            return element.getAsDouble();
        } catch (Exception ignored) {
            return def;
        }
    }

    public enum TargetType {
        ANY, ITEM, MOB, PLAYER, BLOCK
    }

    public record DetectionProfile(
        Identifier requiredPower,
        TargetType targetType,
        Identifier rangeAttribute,
        double baseRange,
        double rangePerAttribute,
        double minRange,
        double maxRange
    ) {}

    private EntityDetectionComponentRegistry() {}
}
