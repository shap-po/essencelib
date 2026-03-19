package com.github.shap_po.essencelib.util;

import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;

public final class DownedIntelHelper {
    public static final int CAUSE_UNKNOWN = 0;
    public static final int CAUSE_PLAYER = 1;
    public static final int CAUSE_MOB = 2;
    public static final int CAUSE_PROJECTILE = 3;
    public static final int CAUSE_FALL = 4;
    public static final int CAUSE_FIRE = 5;
    public static final int CAUSE_MAGIC = 6;
    public static final int CAUSE_EXPLOSION = 7;
    public static final int CAUSE_VOID = 8;
    public static final int CAUSE_DROWN = 9;
    public static final int CAUSE_STARVE = 10;

    public static int countOccupiedEssenceSlots(PlayerEntity player) {
        final int[] count = {0};
        TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
            for (var group : component.getInventory().values()) {
                for (var inv : group.values()) {
                    for (int i = 0; i < inv.size(); i++) {
                        if (inv.getStack(i).getItem() instanceof MobEssenceTrinketItem) {
                            count[0]++;
                        }
                    }
                }
            }
        });
        return count[0];
    }

    public static int resolveCauseCode(DamageSource source) {
        if (source.getAttacker() instanceof net.minecraft.entity.player.PlayerEntity) {
            return CAUSE_PLAYER;
        }
        if (source.getAttacker() instanceof net.minecraft.entity.mob.MobEntity) {
            return CAUSE_MOB;
        }
        if (source.isOf(DamageTypes.ARROW)
            || source.isOf(DamageTypes.TRIDENT)
            || source.isOf(DamageTypes.MOB_PROJECTILE)
            || source.isOf(DamageTypes.FIREBALL)
            || source.isOf(DamageTypes.UNATTRIBUTED_FIREBALL)
            || source.isOf(DamageTypes.WITHER_SKULL)
            || source.isOf(DamageTypes.THROWN)
            || source.isOf(DamageTypes.WIND_CHARGE)) {
            return CAUSE_PROJECTILE;
        }
        if (source.isOf(DamageTypes.FALL)) return CAUSE_FALL;
        if (source.isOf(DamageTypes.IN_FIRE)
            || source.isOf(DamageTypes.ON_FIRE)
            || source.isOf(DamageTypes.LAVA)
            || source.isOf(DamageTypes.HOT_FLOOR)
            || source.isOf(DamageTypes.CAMPFIRE)) {
            return CAUSE_FIRE;
        }
        if (source.isOf(DamageTypes.MAGIC)
            || source.isOf(DamageTypes.INDIRECT_MAGIC)
            || source.isOf(DamageTypes.WITHER)
            || source.isOf(DamageTypes.DRAGON_BREATH)) {
            return CAUSE_MAGIC;
        }
        if (source.isOf(DamageTypes.EXPLOSION)
            || source.isOf(DamageTypes.PLAYER_EXPLOSION)
            || source.isOf(DamageTypes.BAD_RESPAWN_POINT)) {
            return CAUSE_EXPLOSION;
        }
        if (source.isOf(DamageTypes.OUT_OF_WORLD)) return CAUSE_VOID;
        if (source.isOf(DamageTypes.DROWN)) return CAUSE_DROWN;
        if (source.isOf(DamageTypes.STARVE)) return CAUSE_STARVE;
        return CAUSE_UNKNOWN;
    }

    private DownedIntelHelper() {}
}
