package net.dehydration.init;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.dehydration.item.HandbookItem;
import net.dehydration.item.LeatherFlask;
import net.dehydration.item.PurifiedBucket;
import net.dehydration.item.WaterBowlItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemInit {
    // Item Group
    public static final RegistryKey<ItemGroup> DEHYDRATION_ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier("dehydration", "item_group"));
    // Map and List
    private static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();
    public static final List<Item> FLASK_ITEM_LIST = new ArrayList<Item>();
    // Flasks
    public static final Item LEATHER_FLASK = register("leather_flask", new LeatherFlask(0, new Item.Settings().maxCount(1)));
    public static final Item IRON_LEATHER_FLASK = register("iron_leather_flask", new LeatherFlask(1, new Item.Settings().maxCount(1)));
    public static final Item GOLDEN_LEATHER_FLASK = register("golden_leather_flask", new LeatherFlask(2, new Item.Settings().maxCount(1)));
    public static final Item DIAMOND_LEATHER_FLASK = register("diamond_leather_flask", new LeatherFlask(3, new Item.Settings().maxCount(1)));
    public static final Item NETHERITE_LEATHER_FLASK = register("netherite_leather_flask", new LeatherFlask(4, new Item.Settings().maxCount(1).fireproof()));
    // Potion
    public static final Potion PURIFIED_WATER = new Potion(new StatusEffectInstance[0]);
    public static final Potion HYDRATION = new Potion(new StatusEffectInstance(EffectInit.HYDRATION, 900));
    // Handbook
    public static final Item HANDBOOK = register("handbook", new HandbookItem(new Item.Settings()));
    // Bucket
    public static final Item PURIFIED_BUCKET = register("purified_water_bucket", new PurifiedBucket(new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
    // Bowl
    public static final Item WATER_BOWL = register("water_bowl", new WaterBowlItem(new Item.Settings().maxCount(1), true));
    public static final Item PURIFIED_WATER_BOWL = register("purified_water_bowl", new WaterBowlItem(new Item.Settings().maxCount(1), true));

    private static Item register(String name, Item item) {
        ItemGroupEvents.modifyEntriesEvent(DEHYDRATION_ITEM_GROUP).register(entries -> entries.add(item));
        ITEMS.put(new Identifier("dehydration", name), item);
        if (name.contains("flask")) {
            FLASK_ITEM_LIST.add(item);
        }
        return item;
    }

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, DEHYDRATION_ITEM_GROUP,
                FabricItemGroup.builder().icon(() -> new ItemStack(ItemInit.LEATHER_FLASK)).displayName(Text.translatable("item.dehydration.item_group")).build());
        for (Identifier id : ITEMS.keySet()) {
            Registry.register(Registries.ITEM, id, ITEMS.get(id));
        }
        Registry.register(Registries.POTION, "purified_water", PURIFIED_WATER);
        Registry.register(Registries.POTION, "hydration", HYDRATION);
    }

}
