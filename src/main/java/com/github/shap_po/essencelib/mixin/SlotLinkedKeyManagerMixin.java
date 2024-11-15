package com.github.shap_po.essencelib.mixin;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.registry.SlotLinkedKeysGenerator;
import com.github.shap_po.shappoli.integration.trinkets.slk.SlotLinkedKeyManager;
import io.github.apace100.calio.data.MultiJsonDataContainer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(SlotLinkedKeyManager.class)
public class SlotLinkedKeyManagerMixin {
    @Inject(method = "apply(Lio/github/apace100/calio/data/MultiJsonDataContainer;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("HEAD"), remap = false)
    private void apply(MultiJsonDataContainer prepared, ResourceManager manager, Profiler profiler, CallbackInfo ci) {
        Identifier id = EssenceLib.identifier(EssenceLib.MOD_ID);

        Set<MultiJsonDataContainer.Entry> entries = prepared.getOrDefault(id, new LinkedHashSet<>());
        entries.add(new MultiJsonDataContainer.Entry(EssenceLib.MOD_ID, SlotLinkedKeysGenerator.createSlotLinkedKeys(EssenceLib.MAX_SLOT_COUNT)));

        prepared.put(id, entries);
    }
}
