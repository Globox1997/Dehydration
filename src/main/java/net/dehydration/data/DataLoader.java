package net.dehydration.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

public class DataLoader implements SimpleSynchronousResourceReloadListener {

    // Map to store replacing bools
    private HashMap<Integer, Boolean> replaceList = new HashMap<Integer, Boolean>();

    @Override
    public Identifier getFabricId() {
        return new Identifier("dehydration", "loader");
    }

    @Override
    public void reload(ResourceManager manager) {

        manager.findResources("hydration_items", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                for (int i = 1; i <= 20; i++) {
                    replaceList.put(i, false);
                    JsonElement jsonElement = data.get(String.valueOf(i));
                    if (jsonElement != null && jsonElement instanceof JsonObject) {
                        JsonObject jsonObject = (JsonObject) jsonElement;

                        if (JsonHelper.getBoolean(jsonObject, "replace", false))
                            replaceList.replace(i, true);

                        if (jsonObject.getAsJsonArray("items") != null) {
                            if (JsonHelper.getBoolean(jsonObject, "replace", false)) {
                                Iterator<HydrationTemplate> iterator = DehydrationMain.HYDRATION_TEMPLATES.iterator();
                                while (iterator.hasNext()) {
                                    if (iterator.next().getHydration() == i) {
                                        iterator.remove();
                                    }
                                }
                            } else if (replaceList.get(i)) {
                                continue;
                            }

                            List<Item> items = new ArrayList<Item>();
                            for (int u = 0; u < jsonObject.getAsJsonArray("items").size(); u++) {
                                if (!Registries.ITEM.containsId(new Identifier(jsonObject.getAsJsonArray("items").get(u).getAsString()))) {
                                    DehydrationMain.LOGGER.warn("{} is not a valid item identifier", jsonObject.getAsJsonArray("items").get(u).getAsString());
                                    continue;
                                }
                                items.add(Registries.ITEM.get(new Identifier(jsonObject.getAsJsonArray("items").get(u).getAsString())));
                            }
                            DehydrationMain.HYDRATION_TEMPLATES.add(new HydrationTemplate(i, items));

                        }

                    } else
                        continue;
                }

            } catch (Exception e) {
                DehydrationMain.LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });

    }

}
