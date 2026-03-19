package com.github.shap_po.essencelib.registry;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.block.DownedCorpseBlock;
import com.github.shap_po.essencelib.block.GuillotineBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlocks {
    public static final Block DOWNED_CORPSE = Registry.register(
        Registries.BLOCK,
        EssenceLib.identifier("downed_corpse"),
        new DownedCorpseBlock(Block.Settings.copy(Blocks.OAK_PLANKS).nonOpaque().strength(1.0f))
    );

    public static final Block GUILLOTINE = Registry.register(
        Registries.BLOCK,
        EssenceLib.identifier("guillotine"),
        new GuillotineBlock(Block.Settings.copy(Blocks.IRON_BLOCK).nonOpaque())
    );

    public static void register() {
        // Registration done in static init above
    }
}
