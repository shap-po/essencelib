package com.github.shap_po.essencelib.essence;

import com.github.shap_po.essencelib.EssenceLib;
import com.github.shap_po.essencelib.networking.s2c.SyncEssencesS2CPacket;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.apace100.apoli.power.PowerManager;
import io.github.apace100.apoli.util.PrioritizedEntry;
import io.github.apace100.calio.CalioServer;
import io.github.apace100.calio.data.IdentifiableMultiJsonDataLoader;
import io.github.apace100.calio.data.MultiJsonDataContainer;
import io.github.apace100.calio.data.SerializableData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A loader for essence data.
 * <br>
 * The code is heavily based on the {@link io.github.apace100.origins.origin.OriginLayerManager} class.
 */
public class EssenceManager extends IdentifiableMultiJsonDataLoader implements IdentifiableResourceReloadListener {
    public static final Identifier ID = EssenceLib.identifier("essence");

    private static final Object2ObjectOpenHashMap<Identifier, Essence> ESSENCE_BY_ID = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<Identifier, Integer> LOADING_PRIORITIES = new Object2ObjectOpenHashMap<>();

    private static final Gson GSON = new GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create();


    public EssenceManager() {
        super(GSON, "essence", ResourceType.SERVER_DATA);

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ID, PowerManager.ID);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> send(player));
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    protected void apply(MultiJsonDataContainer prepared, ResourceManager manager, Profiler profiler) {
        EssenceLib.LOGGER.info("Reading essences data from data packs...");

        DynamicRegistryManager dynamicRegistries = CalioServer.getDynamicRegistries().orElse(null);
        startBuilding();

        if (dynamicRegistries == null) {
            EssenceLib.LOGGER.error("Can't read essences from data packs without access to dynamic registries!");
            endBuilding();
            return;
        }

        Map<Identifier, List<PrioritizedEntry<Essence>>> loadedEssences = new Object2ObjectOpenHashMap<>();
        prepared.forEach((packName, id, jsonElement) -> {
            try {
                SerializableData.CURRENT_NAMESPACE = id.getNamespace();
                SerializableData.CURRENT_PATH = id.getPath();

                if (!(jsonElement instanceof JsonObject jsonObject)) {
                    throw new JsonSyntaxException("Not a JSON object: " + jsonElement);
                }

                jsonObject.addProperty("id", id.toString());

                Essence essence = Essence.DATA_TYPE.read(dynamicRegistries.getOps(JsonOps.INSTANCE), jsonObject).getOrThrow();
                int currLoadingPriority = JsonHelper.getInt(jsonObject, "loading_priority", 0);

                PrioritizedEntry<Essence> entry = new PrioritizedEntry<>(essence, currLoadingPriority);
                int prevLoadingPriority = LOADING_PRIORITIES.getOrDefault(id, Integer.MIN_VALUE);

                if (essence.shouldReplace() && currLoadingPriority <= prevLoadingPriority) {
                    EssenceLib.LOGGER.warn("Ignoring essence \"{}\" with 'replace' set to true from data pack [{}]. Its loading priority ({}) must be higher than {} to replace the essence!", id, packName, currLoadingPriority, prevLoadingPriority);
                    return; // break
                }

                if (essence.shouldReplace()) {
                    EssenceLib.LOGGER.info("Essence \"{}\" has been replaced by data pack [{}]!", id, packName);
                }

                loadedEssences.computeIfAbsent(id, k -> new LinkedList<>()).add(entry);
                LOADING_PRIORITIES.put(id, currLoadingPriority);

            } catch (Exception e) {
                EssenceLib.LOGGER.error("There was a problem reading essence \"{}\": {}", id, e.getMessage());
            }
        });

        SerializableData.CURRENT_NAMESPACE = null;
        SerializableData.CURRENT_PATH = null;

        EssenceLib.LOGGER.info("Finished reading {} essences. Merging similar ones...", loadedEssences.size());
        loadedEssences.forEach((id, entries) -> {
            AtomicReference<Essence> currentEssence = new AtomicReference<>();
            entries.sort(Comparator.comparing(PrioritizedEntry::priority));

            for (PrioritizedEntry<Essence> entry : entries) {
                if (currentEssence.get() == null) {
                    currentEssence.set(entry.value());
                } else {
                    currentEssence.accumulateAndGet(entry.value(), Essence::merge);
                }
            }

            ESSENCE_BY_ID.put(id, currentEssence.get());
        });

        endBuilding();
        EssenceLib.LOGGER.info("Finished merging similar essences. Total essences: {}", size());
    }

    private static void startBuilding() {
        LOADING_PRIORITIES.clear();
        ESSENCE_BY_ID.clear();
    }

    private static void endBuilding() {
        LOADING_PRIORITIES.clear();
        ESSENCE_BY_ID.trim();
    }

    public static int size() {
        return ESSENCE_BY_ID.size();
    }

    public static DataResult<Essence> getResult(Identifier id) {
        return contains(id)
            ? DataResult.success(ESSENCE_BY_ID.get(id))
            : DataResult.error(() -> "Couldn't get essence from ID \"" + id + "\", as it wasn't registered!");
    }

    public static Optional<Essence> getOptional(Identifier id) {
        return getResult(id).result();
    }

    @Nullable
    public static Essence getNullable(Identifier id) {
        return ESSENCE_BY_ID.get(id);
    }

    public static Essence getOrThrow(Identifier id) {
        return getResult(id).getOrThrow();
    }

    public static Collection<Essence> values() {
        return ESSENCE_BY_ID.values();
    }

    public static boolean contains(Identifier id) {
        return ESSENCE_BY_ID.containsKey(id);
    }

    public static boolean contains(Essence essence) {
        return ESSENCE_BY_ID.containsValue(essence);
    }

    public static ImmutableList<Essence> getAll() {
        return ImmutableList.copyOf(ESSENCE_BY_ID.values());
    }

    public static void send(ServerPlayerEntity player) {
        if (player.server.isDedicated()) {
            ServerPlayNetworking.send(player, new SyncEssencesS2CPacket(ESSENCE_BY_ID));
        }
    }

    @Environment(EnvType.CLIENT)
    public static void receive(SyncEssencesS2CPacket packet) {
        startBuilding();

        packet.essenceById().entrySet()
            .stream()
            .peek(e -> e.getValue().validate())
            .forEach(e -> ESSENCE_BY_ID.put(e.getKey(), e.getValue()));

        endBuilding();
    }
}
