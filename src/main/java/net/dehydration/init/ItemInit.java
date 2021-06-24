package net.dehydration.init;

import java.util.LinkedHashMap;
import java.util.Map;

import net.dehydration.access.ThristManagerAccess;
import net.dehydration.item.Leather_Flask;
import net.dehydration.thirst.ThirstManager;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.Potion;
import net.minecraft.sound.SoundCategory;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class ItemInit {
    private static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();
    // Flasks
    public static final Leather_Flask LEATHER_FLASK = register("leather_flask", new Leather_Flask(0, new Item.Settings().group(ItemGroup.MISC).maxCount(1)));
    public static final Leather_Flask IRON_LEATHER_FLASK = register("iron_leather_flask", new Leather_Flask(1, new Item.Settings().group(ItemGroup.MISC).maxCount(1)));
    public static final Leather_Flask GOLDEN_LEATHER_FLASK = register("golden_leather_flask", new Leather_Flask(2, new Item.Settings().group(ItemGroup.MISC).maxCount(1)));
    public static final Leather_Flask DIAMOND_LEATHER_FLASK = register("diamond_leather_flask", new Leather_Flask(3, new Item.Settings().group(ItemGroup.MISC).maxCount(1)));
    public static final Leather_Flask NETHERITE_LEATHER_FLASK = register("netherite_leather_flask", new Leather_Flask(3, new Item.Settings().group(ItemGroup.MISC).maxCount(1).fireproof()));
    // Potion
    public static final Potion PURIFIED_WATER = new Potion(new StatusEffectInstance[0]);

    private static <I extends Item> I register(String name, I item) {
        ITEMS.put(new Identifier("dehydration", name), item);
        return item;
    }

    public static void init() {
        for (Identifier id : ITEMS.keySet()) {
            Registry.register(Registry.ITEM, id, ITEMS.get(id));
        }
        Registry.register(Registry.POTION, "purified_water", PURIFIED_WATER);

        UseBlockCallback.EVENT.register((player, world, hand, result) -> {
            if (!player.isCreative() && !player.isSpectator() && player.isSneaking() && player.getMainHandStack().isEmpty()) {
                HitResult hitResult = player.raycast(1.5D, 0.0F, true);
                BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                if (world.canPlayerModifyAt(player, blockPos) && world.getFluidState(blockPos).isIn(FluidTags.WATER) && world.getFluidState(blockPos).isStill()) {
                    ThirstManager thirstManager = ((ThristManagerAccess) player).getThirstManager(player);
                    if (thirstManager.isNotFull()) {
                        if (!world.isClient) {
                            thirstManager.add(ConfigInit.CONFIG.water_souce_quench);
                            if (world.random.nextFloat() <= ConfigInit.CONFIG.water_sip_thirst_chance) {
                                player.addStatusEffect(new StatusEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.water_sip_thirst_duration, 0, false, false, true));
                            }
                        }
                        world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundInit.WATER_SIP_EVENT, SoundCategory.PLAYERS, 1.0F, 0.9F + (world.random.nextFloat() / 5F));
                        return ActionResult.SUCCESS;
                    }
                }
                return ActionResult.PASS;
            }
            return ActionResult.PASS;
        });

    }

}
