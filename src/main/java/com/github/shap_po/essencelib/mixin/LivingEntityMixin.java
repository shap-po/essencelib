package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.component.UniqueKillsCounterComponent;
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
        UniqueKillsCounterComponent.getOptional(player).ifPresent(component -> {
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
}
