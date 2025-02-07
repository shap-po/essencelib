package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.component.LevelComponent;
import com.github.shap_po.essencelib.essence.Essence;
import com.github.shap_po.essencelib.essence.EssenceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.shap_po.essencelib.networking.ManaPackets;
import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
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
                ItemStack stack = e.toItemStack();
                this.dropStack(stack);
            });
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        
        if (!entity.getWorld().isClient) {
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
}
