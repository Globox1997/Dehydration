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
                                while (iterator.hasNext())
                                    if (iterator.next().getHydration() == i)
                                        iterator.remove();
                            } else if (replaceList.get(i))
                                continue;

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

            // try {
            // InputStream stream = resourceRef.getInputStream();
            // JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

            // // for (int u = 0; u < data.getAsJsonArray("drops").size(); u++) {
            // // JsonObject data2 = (JsonObject) data.getAsJsonArray("drops").get(u);
            // // if (Registry.BLOCK.get(new Identifier(data2.get("block_id").getAsString())) == Blocks.AIR) {
            // // DehydrationMain.LOGGER.warn("Block Id: " + data2.get("block_id").getAsString() + " is not a valid block id");
            // // continue;
            // // }

            // // Item blockItem = Registry.BLOCK.get(new Identifier(data2.get("block_id").getAsString())).asItem();

            // // List<Item> blockDrops = new ArrayList<Item>();
            // // List<Float> dropChances = new ArrayList<Float>();
            // // List<Integer> rollCount = new ArrayList<Integer>();

            // // if (JsonHelper.getBoolean(data2, "replace", false)) {
            // // for (int i = 0; i < EarlyStageMain.SIEVE_DROP_TEMPLATES.size(); i++)
            // // if (EarlyStageMain.SIEVE_DROP_TEMPLATES.get(i).getBlockItem() == blockItem) {
            // // EarlyStageMain.SIEVE_DROP_TEMPLATES.remove(i);
            // // break;
            // // }
            // // } else {
            // // // checks if it already exist
            // // for (int i = 0; i < EarlyStageMain.SIEVE_DROP_TEMPLATES.size(); i++)
            // // if (EarlyStageMain.SIEVE_DROP_TEMPLATES.get(i).getBlockItem() == blockItem) {
            // // blockDrops.addAll(EarlyStageMain.SIEVE_DROP_TEMPLATES.get(i).getBlockDrops());
            // // dropChances.addAll(EarlyStageMain.SIEVE_DROP_TEMPLATES.get(i).getDropChances());
            // // rollCount.addAll(EarlyStageMain.SIEVE_DROP_TEMPLATES.get(i).getRollCount());
            // // EarlyStageMain.SIEVE_DROP_TEMPLATES.remove(i);
            // // break;
            // // }
            // // }

            // // for (int i = 0; i < data2.getAsJsonArray("block_drops").size(); i++) {
            // // JsonObject data3 = (JsonObject) data2.getAsJsonArray("block_drops").get(i);
            // // blockDrops.add(Registry.ITEM.get(new Identifier(data3.get("item_id").getAsString())));
            // // dropChances.add(data3.get("chance").getAsFloat());
            // // rollCount.add(data3.get("rolls").getAsInt());
            // // }
            // // EarlyStageMain.SIEVE_DROP_TEMPLATES.add(new SieveDropTemplate(blockItem, blockDrops, dropChances, rollCount));
            // // }
            // } catch (Exception e) {
            // DehydrationMain.LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            // }
        });

    }

}
