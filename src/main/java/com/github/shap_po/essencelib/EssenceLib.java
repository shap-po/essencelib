package com.github.shap_po.essencelib;

import com.github.shap_po.essencelib.component.CollectorRushComponent;
import com.github.shap_po.essencelib.component.CollectorRushComponentImpl;
import com.github.shap_po.essencelib.component.DownedComponent;
import com.github.shap_po.essencelib.component.DownedComponentImpl;
import com.github.shap_po.essencelib.component.LevelComponent;
import com.github.shap_po.essencelib.component.RestrainedComponent;
import com.github.shap_po.essencelib.component.RestrainedComponentImpl;
import com.github.shap_po.essencelib.component.LevelComponentImpl;
import com.github.shap_po.essencelib.action.type.EssenceLibEntityActionTypes;
import com.github.shap_po.essencelib.action.type.EssenceLibBiEntityActionTypes;
import com.github.shap_po.essencelib.block.entity.DownedCorpseBlockEntity;
import com.github.shap_po.essencelib.power.EssenceLibPowerTypes;
import com.github.shap_po.essencelib.condition.EssenceLibConditionTypes;
import com.github.shap_po.essencelib.essence.EssenceManager;
import com.github.shap_po.essencelib.item.MobEssenceTrinketItem;
import com.github.shap_po.essencelib.level.LevelManager;
import com.github.shap_po.essencelib.loot.function.ModLootFunctionTypes;
import com.github.shap_po.essencelib.networking.ModPackets;
import com.github.shap_po.essencelib.networking.ModPacketsC2S;
import com.github.shap_po.essencelib.registry.EssenceLibParticles;
import com.github.shap_po.essencelib.registry.ManaAttributeRegistry;
import com.github.shap_po.essencelib.registry.ModBlockEntities;
import com.github.shap_po.essencelib.registry.ModBlocks;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.essencelib.registry.ModItems;
import com.github.shap_po.essencelib.registry.ModScreenHandlers;
import com.github.shap_po.essencelib.registry.ModSounds;
import com.github.shap_po.essencelib.entity.NuisanceAllayHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.lang.reflect.Method;

public class EssenceLib implements ModInitializer, EntityComponentInitializer {
    public static final String MOD_ID = "essencelib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final String KEYBINDINGS_CATEGORY = "key.category." + EssenceLib.MOD_ID;
    /** Maximum essence slots / keybinds; matches {@link LevelManager#MAX_LEVEL}. */
    public static final int MAX_SLOT_COUNT = LevelManager.MAX_LEVEL;
    private static final Map<UUID, Integer> SLOT_SYNC_RETRIES = new HashMap<>();

    public static Identifier identifier(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing EssenceLib...");
        // #region agent log
        NuisanceAllayHelper.debugLogGlobal(
            "H0",
            "EssenceLib#onInitialize",
            "Mod initializer reached",
            "{\"modId\":\"essencelib\"}"
        );
        // #endregion

        // Register everything in proper order
        ModDataComponentTypes.registerDataComponentTypes();
        EssenceLibParticles.register();
        ManaAttributeRegistry.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        ModSounds.register();
        ModScreenHandlers.register();

        // Register attributes
        LOGGER.debug("Registering attributes...");
        ManaAttributeRegistry.register();

        // Register Apoli additions
        EssenceLibConditionTypes.register();
        EssenceLibEntityActionTypes.register();
        EssenceLibBiEntityActionTypes.register();
        EssenceLibPowerTypes.register();

        // Register networking before it's needed
        LOGGER.debug("Setting up networking...");
        ModPackets.register();
        ModPacketsC2S.register();

        // Register other components
        LOGGER.debug("Registering components...");
        ModLootFunctionTypes.register();

        // Register resource reloaders
        LOGGER.debug("Registering resource reloaders...");
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new EssenceManager());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new LevelManager());

        // Apply slot count and sync to client on join so both sides have matching slot counts
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LevelComponent comp = LevelComponent.KEY.get(handler.player);
            if (comp != null) comp.updateLevel(true);
            // Retry for a short period because Trinkets inventory/handler can initialize a few ticks later.
            SLOT_SYNC_RETRIES.put(handler.player.getUuid(), 60);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (!DownedComponent.isDowned(handler.player)) {
                return;
            }
            DownedComponent comp = DownedComponent.getNullable(handler.player);
            if (comp != null && comp.hasCorpseLocation()) {
                return;
            }
            spawnDownedCorpse(handler.player);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (SLOT_SYNC_RETRIES.isEmpty()) return;
            Iterator<Map.Entry<UUID, Integer>> it = SLOT_SYNC_RETRIES.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, Integer> e = it.next();
                var player = server.getPlayerManager().getPlayer(e.getKey());
                if (player == null) {
                    it.remove();
                    continue;
                }
                LevelComponent comp = LevelComponent.KEY.get(player);
                if (comp != null) comp.updateLevel(true);

                int remaining = e.getValue() - 1;
                if (remaining <= 0) it.remove();
                else e.setValue(remaining);
            }
        });

        // Register commands last
        LOGGER.debug("Registering commands...");
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            try {
                // Reflective load guards against stale/dev classpath edge-cases in split source-set runs.
                Class<?> cmdClass = Class.forName("com.github.shap_po.essencelib.command.EssenceLibCommand");
                Method register = cmdClass.getMethod("register", com.mojang.brigadier.CommandDispatcher.class);
                register.invoke(null, dispatcher);
            } catch (Throwable t) {
                LOGGER.error("Failed to register EssenceLib commands; continuing without /essencelib commands.", t);
            }
        });

        LOGGER.info("EssenceLib initialized successfully!");
    }

    public static void spawnDownedCorpse(net.minecraft.server.network.ServerPlayerEntity player) {
        DownedComponent downedComponent = DownedComponent.getNullable(player);
        if (downedComponent != null && downedComponent.hasCorpseLocation()) {
            return;
        }

        BlockPos base = player.getBlockPos();
        BlockPos placePos = essencelib$findCorpsePlacement(player.getServerWorld(), base);
        if (placePos == null) {
            return;
        }

        player.getServerWorld().setBlockState(placePos, ModBlocks.DOWNED_CORPSE.getDefaultState());
        if (!(player.getServerWorld().getBlockEntity(placePos) instanceof DownedCorpseBlockEntity corpse)) {
            return;
        }

        corpse.setOwnerName(player.getName().getString());
        corpse.setOwnerUuid(player.getUuid());
        if (downedComponent != null) {
            downedComponent.setCorpseLocation(player.getServerWorld().getRegistryKey().getValue(), placePos);
            corpse.setIntel(
                downedComponent.getEssenceSlotsOccupied(),
                downedComponent.getLastDownedCauseCode(),
                downedComponent.getLastDownedEpochSeconds()
            );
        }

        // Copy non-essence inventory slots to corpse and remove them from player.
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().main.get(i);
            if (essencelib$isProtectedEssence(stack)) continue;
            corpse.setStack(i, stack.copy());
            player.getInventory().main.set(i, ItemStack.EMPTY);
        }
        for (int i = 0; i < 4; i++) {
            ItemStack stack = player.getInventory().armor.get(i);
            if (essencelib$isProtectedEssence(stack)) continue;
            corpse.setStack(36 + i, stack.copy());
            player.getInventory().armor.set(i, ItemStack.EMPTY);
        }
        ItemStack offhand = player.getInventory().offHand.getFirst();
        if (!essencelib$isProtectedEssence(offhand)) {
            corpse.setStack(40, offhand.copy());
            player.getInventory().offHand.set(0, ItemStack.EMPTY);
        }

        // Also move non-essence trinkets to corpse if free slots exist.
        TrinketsApi.getTrinketComponent(player).ifPresent(comp -> {
            for (var group : comp.getInventory().values()) {
                for (var inv : group.values()) {
                    for (int slot = 0; slot < inv.size(); slot++) {
                        ItemStack stack = inv.getStack(slot);
                        if (stack.isEmpty() || essencelib$isProtectedEssence(stack)) continue;
                        int free = essencelib$findFirstEmptyCorpseSlot(corpse);
                        if (free < 0) return;
                        corpse.setStack(free, stack.copy());
                        inv.setStack(slot, ItemStack.EMPTY);
                    }
                }
            }
        });

        player.getInventory().markDirty();
        player.playerScreenHandler.sendContentUpdates();
        corpse.markDirty();
    }

    private static boolean essencelib$isProtectedEssence(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof MobEssenceTrinketItem;
    }

    private static int essencelib$findFirstEmptyCorpseSlot(DownedCorpseBlockEntity corpse) {
        for (int i = 0; i < DownedCorpseBlockEntity.LOOT_SIZE; i++) {
            if (corpse.getStack(i).isEmpty()) return i;
        }
        return -1;
    }

    private static BlockPos essencelib$findCorpsePlacement(net.minecraft.server.world.ServerWorld world, BlockPos base) {
        if (world.getBlockState(base).isAir()) return base;
        for (int y = 0; y <= 1; y++) {
            for (int r = 1; r <= 2; r++) {
                for (int dx = -r; dx <= r; dx++) {
                    for (int dz = -r; dz <= r; dz++) {
                        BlockPos candidate = base.add(dx, y, dz);
                        if (world.getBlockState(candidate).isAir()) {
                            return candidate;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(LevelComponent.KEY, LevelComponentImpl::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(CollectorRushComponent.KEY, CollectorRushComponentImpl::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(DownedComponent.KEY, DownedComponentImpl::new, RespawnCopyStrategy.NEVER_COPY);
        registry.registerForPlayers(RestrainedComponent.KEY, RestrainedComponentImpl::new, RespawnCopyStrategy.NEVER_COPY);
    }
}
