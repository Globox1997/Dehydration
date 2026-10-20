package net.dehydration.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dehydration.DehydrationMain;
import net.dehydration.api.HydrationTemplate;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DataLoader implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return DehydrationMain.identifierOf("loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        List<Set<Item>> additiveItems = new ArrayList<>();
        List<Set<Item>> replacingItems = new ArrayList<>();
        boolean[] replaced = new boolean[21];
        for (int i = 0; i <= 20; i++) {
            additiveItems.add(new LinkedHashSet<>());
            replacingItems.add(new LinkedHashSet<>());
        }

        manager.findResources("hydration_items", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try (Reader reader = new InputStreamReader(resourceRef.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject data = JsonParser.parseReader(reader).getAsJsonObject();

                for (int i = 1; i <= 20; i++) {
                    JsonElement jsonElement = data.get(String.valueOf(i));
                    if (!(jsonElement instanceof JsonObject jsonObject)) {
                        continue;
                    }
                    boolean replace = JsonHelper.getBoolean(jsonObject, "replace", false);
                    if (replace) {
                        replaced[i] = true;
                    }
                    JsonArray itemArray = jsonObject.getAsJsonArray("items");
                    if (itemArray == null) {
                        continue;
                    }
                    Set<Item> target = replace ? replacingItems.get(i) : additiveItems.get(i);
                    for (JsonElement itemElement : itemArray) {
                        String itemId = itemElement.getAsString();
                        Identifier identifier = Identifier.tryParse(itemId);
                        if (identifier == null || !Registries.ITEM.containsId(identifier)) {
                            DehydrationMain.LOGGER.warn("{} is not a valid item identifier", itemId);
                            continue;
                        }
                        target.add(Registries.ITEM.get(identifier));
                    }
                }

            } catch (Exception e) {
                DehydrationMain.LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });

        List<HydrationTemplate> templates = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Set<Item> items = replaced[i] ? replacingItems.get(i) : additiveItems.get(i);
            if (!items.isEmpty()) {
                templates.add(new HydrationTemplate(i, new ArrayList<>(items)));
            }
        }
        DehydrationMain.HYDRATION_TEMPLATES.clear();
        DehydrationMain.HYDRATION_TEMPLATES.addAll(templates);
    }

}
