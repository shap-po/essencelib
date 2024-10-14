package com.github.shap_po.essencelib.essence;

import com.github.shap_po.essencelib.EssenceLib;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import io.github.apace100.apoli.util.PrioritizedEntry;
import io.github.apace100.calio.CalioServer;
import io.github.apace100.calio.data.IdentifiableMultiJsonDataLoader;
import io.github.apace100.calio.data.MultiJsonDataContainer;
import io.github.apace100.calio.data.SerializableData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
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
public class EssenceLoader extends IdentifiableMultiJsonDataLoader implements IdentifiableResourceReloadListener {
    public static final Identifier ID = EssenceLib.identifier("essence");

    private static final Object2ObjectOpenHashMap<Identifier, Essence> ESSENCE_BY_ID = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<Identifier, Integer> LOADING_PRIORITIES = new Object2ObjectOpenHashMap<>();

    private static final Gson GSON = new GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create();


    public EssenceLoader() {
        super(GSON, "essence", ResourceType.SERVER_DATA);
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

                Essence layer = Essence.DATA_TYPE.read(dynamicRegistries.getOps(JsonOps.INSTANCE), jsonObject).getOrThrow();
                int currLoadingPriority = JsonHelper.getInt(jsonObject, "loading_priority", 0);

                PrioritizedEntry<Essence> entry = new PrioritizedEntry<>(layer, currLoadingPriority);
                int prevLoadingPriority = LOADING_PRIORITIES.getOrDefault(id, Integer.MIN_VALUE);

                if (layer.shouldReplace() && currLoadingPriority <= prevLoadingPriority) {
                    EssenceLib.LOGGER.warn("Ignoring essence \"{}\" with 'replace' set to true from data pack [{}]. Its loading priority ({}) must be higher than {} to replace the essence!", id, packName, currLoadingPriority, prevLoadingPriority);
                    return; // break
                }

                if (layer.shouldReplace()) {
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
                    currentEssence.accumulateAndGet(entry.value(), EssenceLoader::merge);
                }

            }

            ESSENCE_BY_ID.put(id, currentEssence.get());

        });

        endBuilding();
        EssenceLib.LOGGER.info("Finished merging similar essences. Total essences: {}", size());
    }

    void startBuilding() {
        LOADING_PRIORITIES.clear();
        ESSENCE_BY_ID.clear();
    }

    void endBuilding() {
        LOADING_PRIORITIES.clear();
        ESSENCE_BY_ID.trim();
    }

    private static Essence merge(Essence oldEssence, Essence newEssence) {
        if (newEssence.shouldReplace()) {
            return newEssence;
        }
        // TODO: implement essence merging
        return newEssence;
    }


    public static int size() {
        return ESSENCE_BY_ID.size();
    }

    @Nullable
    public static Essence getNullable(Identifier id) {
        return ESSENCE_BY_ID.get(id);
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


    // TODO: implement sending & receiving essence data to/from clients
}
