package net.dehydration.init;

import net.dehydration.DehydrationMain;
import net.dehydration.block.entity.BambooPumpEntity;
import net.dehydration.item.HandbookItem;
import net.dehydration.item.LeatherFlask;
import net.dehydration.item.WaterBowlItem;
import net.dehydration.item.component.FlaskComponent;
import net.dehydration.item.storage.BambooPumpItemStorage;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class ItemInit {
    // Item Group
    public static final RegistryKey<ItemGroup> DEHYDRATION_ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of("dehydration", "item_group"));
    // Map and List
    private static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();
    public static final List<Item> FLASK_ITEM_LIST = new ArrayList<Item>();
    // Components
    public static final ComponentType<Integer> COOLDOWN = registerComponent("cooldown", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));
    public static final ComponentType<FlaskComponent> FLASK_DATA = registerComponent("fill_level", builder -> builder.codec(FlaskComponent.CODEC).packetCodec(FlaskComponent.PACKET_CODEC));
    // Flasks
    public static final Item LEATHER_FLASK = register("leather_flask", new LeatherFlask(0, new Item.Settings().maxCount(1)));
    public static final Item IRON_LEATHER_FLASK = register("iron_leather_flask", new LeatherFlask(1, new Item.Settings().maxCount(1)));
    public static final Item GOLDEN_LEATHER_FLASK = register("golden_leather_flask", new LeatherFlask(2, new Item.Settings().maxCount(1)));
    public static final Item DIAMOND_LEATHER_FLASK = register("diamond_leather_flask", new LeatherFlask(3, new Item.Settings().maxCount(1)));
    public static final Item NETHERITE_LEATHER_FLASK = register("netherite_leather_flask", new LeatherFlask(4, new Item.Settings().maxCount(1).fireproof()));
    // Potion
    public static final RegistryEntry<Potion> PURIFIED_WATER = registerPotion("purified_water", new Potion(new StatusEffectInstance[0]));
    public static final RegistryEntry<Potion> HYDRATION = registerPotion("hydration", new Potion(new StatusEffectInstance(EffectInit.HYDRATION, 900)));
    // Handbook
    public static final Item HANDBOOK = register("handbook", new HandbookItem(new Item.Settings()));
    // Bucket
    public static final Item PURIFIED_BUCKET = register("purified_water_bucket", new BucketItem(FluidInit.PURIFIED_WATER, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
    // Bowl
    public static final Item WATER_BOWL = register("water_bowl", new WaterBowlItem(new Item.Settings().maxCount(1), true));
    public static final Item PURIFIED_WATER_BOWL = register("purified_water_bowl", new WaterBowlItem(new Item.Settings().maxCount(1), false));

    private static Item register(String name, Item item) {
        ItemGroupEvents.modifyEntriesEvent(DEHYDRATION_ITEM_GROUP).register(entries -> entries.add(item));
        ITEMS.put(DehydrationMain.identifierOf(name), item);
        if (name.contains("flask")) {
            FLASK_ITEM_LIST.add(item);
        }
        return item;
    }

    private static RegistryEntry<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(name), potion);
    }

    private static <T> ComponentType<T> registerComponent(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, DEHYDRATION_ITEM_GROUP,
                FabricItemGroup.builder().icon(() -> new ItemStack(ItemInit.LEATHER_FLASK)).displayName(Text.translatable("item.dehydration.item_group")).build());
        for (Identifier id : ITEMS.keySet()) {
            Registry.register(Registries.ITEM, id, ITEMS.get(id));
        }
        // register item storage for bamboo pump
        ItemStorage.SIDED.registerForBlockEntities((blockEntity, context) -> new BambooPumpItemStorage((BambooPumpEntity) blockEntity), BlockInit.BAMBOO_PUMP_ENTITY);
    }

}
