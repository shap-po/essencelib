package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.block.GuillotineBlockEntity;
import com.github.shap_po.essencelib.block.entity.DownedCorpseBlockEntity;
import com.github.shap_po.essencelib.registry.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static final BlockEntityType<DownedCorpseBlockEntity> DOWNED_CORPSE = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        EssenceLib.identifier("downed_corpse"),
        FabricBlockEntityTypeBuilder.create(DownedCorpseBlockEntity::new, ModBlocks.DOWNED_CORPSE).build()
    );

    public static final BlockEntityType<GuillotineBlockEntity> GUILLOTINE = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        EssenceLib.identifier("guillotine"),
        FabricBlockEntityTypeBuilder.create(GuillotineBlockEntity::new, ModBlocks.GUILLOTINE).build()
    );

    public static void register() {
        // Registration done in static init above
    }
}
