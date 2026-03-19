package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.component.DownedComponent;
import com.github.shap_po.essencelib.component.LevelComponent;
import com.github.shap_po.essencelib.essence.Essence;
import com.github.shap_po.essencelib.essence.EssenceManager;
import com.github.shap_po.essencelib.util.DownedIntelHelper;
import com.github.shap_po.essencelib.util.DownedStateHelper;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.shap_po.essencelib.networking.ManaPackets;
import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import com.github.shap_po.essencelib.registry.ModItems;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private static final String HARDCORE_MOB_SCALED_TAG = "essencelib_hardcore_mob_scaled";
    private static final String MOB_MANA_PROFILE_TAG = "essencelib_mob_mana_profile_applied";
    private static final double ESSENCE_TO_MOB_SCALAR = 0.65;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void essencelib$countKill(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!(source.getAttacker() instanceof PlayerEntity player)) {
            return;
        }
        LevelComponent.getOptional(player).ifPresent(component -> {
            component.addUniqueKill(this);
        });
    }

    /** When a player would die, put them in downed state instead: clear aggro, set health, cancel onDeath. */
    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"), cancellable = true)
    private void essencelib$downedInsteadOfDeath(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof PlayerEntity player)) return;
        // Allow explicit command kills to behave like normal Minecraft death.
        if (source.isOf(DamageTypes.GENERIC_KILL)) return;
        DownedComponent comp = DownedComponent.getNullable(player);
        if (comp == null || comp.isDowned()) return;
        comp.setDowned(true);
        comp.setEssenceSlotsOccupied(DownedIntelHelper.countOccupiedEssenceSlots(player));
        comp.setLastDownedCauseCode(DownedIntelHelper.resolveCauseCode(source));
        comp.setLastDownedEpochSeconds((int) (System.currentTimeMillis() / 1000L));
        Entity attacker = source.getAttacker();
        comp.setLastDownedKillerUuid(attacker != null ? attacker.getUuid() : null);
        if (attacker instanceof LivingEntity livingAttacker) {
            livingAttacker.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.GLOWING,
                40,
                0,
                true,
                false
            ));
        }
        self.setHealth(1.0f);
        DownedStateHelper.clearMobAggroAround(player, 64.0D);
        if (player instanceof ServerPlayerEntity serverPlayer) {
            EssenceLib.spawnDownedCorpse(serverPlayer);
        }
        cir.cancel();
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void essencelib$blockDamageWhenDowned(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (source.isOf(DamageTypes.GENERIC_KILL)) return;
        if (self instanceof PlayerEntity player && DownedComponent.isDowned(player)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "dropLoot", at = @At(value = "HEAD"))
    private void essencelib$dropEssence(DamageSource damageSource, boolean causedByPlayer, CallbackInfo ci) {
        // TODO: use hashmap for entity -> essence
        EssenceManager.getAll().stream()
            .filter(Essence::hasDropRules)
            .filter(e -> this.getType().equals(e.getDroppedBy()))
            .forEach(e -> {
                if (e.getChance() == null || e.getChance() <= this.getRandom().nextDouble()) {
                    return;
                }
                ItemStack stack = new ItemStack(ModItems.MOB_ESSENCE_ITEM);
                e.applyToItemStack(stack, this.getRandom());
                this.dropStack(stack);
            });
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        
        if (!entity.getWorld().isClient) {
            if (entity instanceof MobEntity mob) {
                essencelib$applyEssenceBasedMobScaling(mob);
                essencelib$applyMobManaProfile(mob);
            }

            // Get attribute values
            double maxMana = entity.getAttributeValue(ManaAttributeRegistry.getMaxManaEntry());
            double currentMana = entity.getAttributeValue(ManaAttributeRegistry.getCurrentManaEntry());
            double regenRate = entity.getAttributeValue(ManaAttributeRegistry.getManaRegenEntry());
            
            // Convert regen rate from mana/minute to mana/tick
            // Scale regen rate: each +1 adds 5 mana per minute
            double scaledRegen = regenRate * 5.0;
            double regenPerTick = scaledRegen / 1200.0;
            
            // Regenerate mana
            if (currentMana < maxMana) {
                double newMana = Math.min(currentMana + regenPerTick, maxMana);
                entity.getAttributeInstance(ManaAttributeRegistry.getCurrentManaEntry()).setBaseValue(newMana);
            }
        }

        // Sync with client
        if (entity instanceof ServerPlayerEntity player) {
            double current = entity.getAttributeValue(ManaAttributeRegistry.getCurrentManaEntry());
            double max = entity.getAttributeValue(ManaAttributeRegistry.getMaxManaEntry());
            ManaPackets.sendManaUpdate(player, (float) current, (float) max);
        }
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void addAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.getReturnValue()
            .add(ManaAttributeRegistry.getMaxManaEntry())
            .add(ManaAttributeRegistry.getCurrentManaEntry())
            .add(ManaAttributeRegistry.getManaRegenEntry());
    }

    private static void essencelib$applyEssenceBasedMobScaling(MobEntity mob) {
        if (mob.getCommandTags().contains(HARDCORE_MOB_SCALED_TAG)) return;

        double oldMaxHealth = mob.getMaxHealth();
        boolean appliedAny = false;

        for (Essence essence : EssenceManager.getAll()) {
            if (essence.getDroppedBy() != mob.getType()) continue;
            for (AttributedEntityAttributeModifier attributed : essence.getAttributes()) {
                if (!essencelib$isCombatAttribute(attributed)) continue;

                EntityAttributeModifier source = attributed.modifier();
                double scaledAmount = source.value() * ESSENCE_TO_MOB_SCALAR;
                if (scaledAmount <= 0.0) continue; // Keep hardcore direction: buffs only.

                EntityAttributeInstance instance = mob.getAttributeInstance(attributed.attribute());
                if (instance == null) continue;

                Identifier modId = essencelib$scaledModId(source.id());
                if (instance.hasModifier(modId)) continue;
                instance.addPersistentModifier(new EntityAttributeModifier(modId, scaledAmount, source.operation()));
                appliedAny = true;
            }
        }

        double newMaxHealth = mob.getMaxHealth();
        if (appliedAny && newMaxHealth > 0.0) {
            float scaledHealth = oldMaxHealth > 0.0
                ? (float) Math.min(newMaxHealth, mob.getHealth() * (newMaxHealth / oldMaxHealth))
                : (float) newMaxHealth;
            mob.setHealth(scaledHealth);
        }

        mob.addCommandTag(HARDCORE_MOB_SCALED_TAG);
    }

    /**
     * Initializes hidden mob mana channels from base attributes + essence-derived bonuses.
     * This is the server-side foundation for future mob mana-consuming behaviors (sprint, skills, drains).
     */
    private static void essencelib$applyMobManaProfile(MobEntity mob) {
        if (mob.getCommandTags().contains(MOB_MANA_PROFILE_TAG)) return;

        double baseHealth = Math.max(1.0, mob.getMaxHealth());
        double baseDamage = Math.max(0.0, essencelib$getAttributeOrDefault(mob, EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0));
        double baseArmor = Math.max(0.0, essencelib$getAttributeOrDefault(mob, EntityAttributes.GENERIC_ARMOR, 0.0));
        double baseSpeed = Math.max(0.0, essencelib$getAttributeOrDefault(mob, EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0));

        // Base hidden channels from vanilla/default mob profile.
        double maxMana = 25.0 + (baseHealth * 1.10) + (baseDamage * 7.0) + (baseArmor * 3.5);
        double manaRegen = 0.35 + (baseDamage * 0.03) + (baseSpeed * 1.20);

        // Apply essence context for this mob type so stat identity carries into mob internals.
        for (Essence essence : EssenceManager.getAll()) {
            if (essence.getDroppedBy() != mob.getType()) continue;
            for (AttributedEntityAttributeModifier attributed : essence.getAttributes()) {
                EntityAttributeModifier source = attributed.modifier();
                double scaled = source.value() * ESSENCE_TO_MOB_SCALAR;
                if (scaled <= 0.0) continue;

                if (attributed.attribute().equals(EntityAttributes.GENERIC_MAX_HEALTH)) {
                    maxMana += scaled * 35.0;
                } else if (attributed.attribute().equals(EntityAttributes.GENERIC_ATTACK_DAMAGE)) {
                    maxMana += scaled * 24.0;
                    manaRegen += scaled * 0.18;
                } else if (attributed.attribute().equals(EntityAttributes.GENERIC_MOVEMENT_SPEED)) {
                    manaRegen += scaled * 0.30;
                } else if (attributed.attribute().equals(EntityAttributes.GENERIC_ARMOR)
                    || attributed.attribute().equals(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)
                    || attributed.attribute().equals(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)) {
                    maxMana += scaled * 12.0;
                }
            }
        }

        EntityAttributeInstance maxManaInst = mob.getAttributeInstance(ManaAttributeRegistry.getMaxManaEntry());
        EntityAttributeInstance currentManaInst = mob.getAttributeInstance(ManaAttributeRegistry.getCurrentManaEntry());
        EntityAttributeInstance regenInst = mob.getAttributeInstance(ManaAttributeRegistry.getManaRegenEntry());
        if (maxManaInst == null || currentManaInst == null || regenInst == null) return;

        double finalMaxMana = Math.max(1.0, Math.min(1000.0, maxMana));
        double finalRegen = Math.max(0.05, Math.min(100.0, manaRegen));

        maxManaInst.setBaseValue(finalMaxMana);
        currentManaInst.setBaseValue(finalMaxMana);
        regenInst.setBaseValue(finalRegen);
        mob.addCommandTag(MOB_MANA_PROFILE_TAG);
    }

    private static double essencelib$getAttributeOrDefault(MobEntity mob, net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> attribute, double fallback) {
        EntityAttributeInstance instance = mob.getAttributeInstance(attribute);
        return instance != null ? instance.getValue() : fallback;
    }

    private static boolean essencelib$isCombatAttribute(AttributedEntityAttributeModifier attributed) {
        return attributed.attribute().equals(EntityAttributes.GENERIC_MAX_HEALTH)
            || attributed.attribute().equals(EntityAttributes.GENERIC_ATTACK_DAMAGE)
            || attributed.attribute().equals(EntityAttributes.GENERIC_MOVEMENT_SPEED)
            || attributed.attribute().equals(EntityAttributes.GENERIC_ARMOR)
            || attributed.attribute().equals(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)
            || attributed.attribute().equals(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
    }

    private static Identifier essencelib$scaledModId(Identifier sourceId) {
        String hashed = Integer.toHexString(sourceId.toString().hashCode());
        return Identifier.of("essencelib", "mob_scaled/" + hashed);
    }
}
