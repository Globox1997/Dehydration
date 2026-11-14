package net.dehydration.init;

import net.dehydration.DehydrationMain;
import net.dehydration.item.HandbookItem;
import net.dehydration.item.LeatherFlask;
import net.dehydration.item.WaterBowlItem;
import net.dehydration.item.component.FlaskComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

public class ItemInit {
    // Item Group
    public static final ResourceKey<CreativeModeTab> DEHYDRATION_ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath("dehydration", "item_group"));
    // Map and List
    private static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();
    public static final List<Item> ITEM_GROUP_ITEMS = new ArrayList<Item>();
    public static final List<Item> FLASK_ITEM_LIST = new ArrayList<Item>();
    // Components
    public static final DataComponentType<Integer> COOLDOWN = createComponent(builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DataComponentType<FlaskComponent> FLASK_DATA = createComponent(builder -> builder.persistent(FlaskComponent.CODEC).networkSynchronized(FlaskComponent.PACKET_CODEC));
    // Flasks
    public static final Item LEATHER_FLASK = register("leather_flask", settings -> new LeatherFlask(0, settings), new Item.Properties().stacksTo(1));
    public static final Item IRON_LEATHER_FLASK = register("iron_leather_flask", settings -> new LeatherFlask(1, settings), new Item.Properties().stacksTo(1));
    public static final Item GOLDEN_LEATHER_FLASK = register("golden_leather_flask", settings -> new LeatherFlask(2, settings), new Item.Properties().stacksTo(1));
    public static final Item DIAMOND_LEATHER_FLASK = register("diamond_leather_flask", settings -> new LeatherFlask(3, settings), new Item.Properties().stacksTo(1));
    public static final Item NETHERITE_LEATHER_FLASK = register("netherite_leather_flask", settings -> new LeatherFlask(4, settings), new Item.Properties().stacksTo(1).fireResistant());
    // Potion
    public static Holder<Potion> PURIFIED_WATER;
    public static Holder<Potion> HYDRATION;
    // Handbook
    public static final Item HANDBOOK = register("handbook", HandbookItem::new, new Item.Properties());
    // Bucket
    public static final Item PURIFIED_BUCKET = register("purified_water_bucket", settings -> new BucketItem(FluidInit.PURIFIED_WATER, settings), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    // Bowl
    public static final Item WATER_BOWL = register("water_bowl", settings -> new WaterBowlItem(settings, true), new Item.Properties().stacksTo(1));
    public static final Item PURIFIED_WATER_BOWL = register("purified_water_bowl", settings -> new WaterBowlItem(settings, false), new Item.Properties().stacksTo(1));

    private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties settings) {
        Identifier id = DehydrationMain.identifierOf(name);
        Item item = factory.apply(settings.setId(ResourceKey.create(Registries.ITEM, id)));
        ITEM_GROUP_ITEMS.add(item);
        ITEMS.put(id, item);
        if (name.contains("flask")) {
            FLASK_ITEM_LIST.add(item);
        }
        return item;
    }

    private static <T> DataComponentType<T> createComponent(UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return builderOperator.apply(DataComponentType.builder()).build();
    }

    public static void registerComponents(BiConsumer<Identifier, DataComponentType<?>> registrar) {
        registrar.accept(Identifier.parse("cooldown"), COOLDOWN);
        registrar.accept(Identifier.parse("fill_level"), FLASK_DATA);
    }

    public static void registerItems(BiConsumer<Identifier, Item> registrar) {
        ITEMS.forEach(registrar);
        BlockInit.registerBlockItems(registrar);
    }

    public static void registerPotions(BiConsumer<Identifier, Potion> registrar) {
        Potion purifiedWater = new Potion("purified_water");
        Potion hydration = new Potion("hydration", new MobEffectInstance(EffectInit.HYDRATION, 900));
        registrar.accept(Identifier.parse("purified_water"), purifiedWater);
        registrar.accept(Identifier.parse("hydration"), hydration);
        PURIFIED_WATER = BuiltInRegistries.POTION.wrapAsHolder(purifiedWater);
        HYDRATION = BuiltInRegistries.POTION.wrapAsHolder(hydration);
    }

}
