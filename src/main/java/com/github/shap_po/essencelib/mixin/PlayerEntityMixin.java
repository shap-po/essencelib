package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.networking.ManaPackets;
import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    private float lastCurrentMana = -1;
    private float lastMaxMana = -1;

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
            // Set base regen to 10 mana per minute (better starting point)
            if (manaRegen.getBaseValue() <= 0) manaRegen.setBaseValue(10.0);

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
        if (player instanceof ServerPlayerEntity serverPlayer) {
            float current = (float) player.getAttributeValue(ManaAttributeRegistry.getCurrentManaEntry());
            float max = (float) player.getAttributeValue(ManaAttributeRegistry.getMaxManaEntry());

            // Only send update if values changed
            if (current != lastCurrentMana || max != lastMaxMana) {
                ManaPackets.sendManaUpdate(serverPlayer, current, max);
                lastCurrentMana = current;
                lastMaxMana = max;
            }
        }
    }
}
