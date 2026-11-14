package net.dehydration.init;

import net.minecraft.world.InteractionResult;
import net.dehydration.access.PlayerAccess;
import net.dehydration.access.ServerPlayerAccess;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.network.ThirstServerPacket;
import net.dehydration.thirst.ThirstManager;
import net.dehydration.util.ThirstHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import java.util.function.Consumer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EventInit {

    public static void onPlayerJoin(ServerPlayer player) {
        ThirstServerPacket.writeS2CExcludedSyncPacket(player, ((ThirstManagerAccess) player).getThirstManager().hasThirst());
        ThirstServerPacket.writeS2CHydrationTemplateSyncPacket(player);
    }

    public static void onPlayerChangeLevel(ServerPlayer player) {
        ((ServerPlayerAccess) player).compatSync();
    }

    public static void onPlayerCopy(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        ((ThirstManagerAccess) newPlayer).getThirstManager().setThirst(((ThirstManagerAccess) oldPlayer).getThirstManager().hasThirst());
        if (alive) {
            ((ThirstManagerAccess) newPlayer).getThirstManager().setThirstLevel(((ThirstManagerAccess) oldPlayer).getThirstManager().getThirstLevel());
            ((ServerPlayerAccess) newPlayer).setSyncedThirstLevel(-1);
        }
    }

    public static void onPlayerRespawn(ServerPlayer newPlayer) {
        ThirstServerPacket.writeS2CExcludedSyncPacket(newPlayer, ((ThirstManagerAccess) newPlayer).getThirstManager().hasThirst());
    }

    public static void modifyLootTable(ResourceKey<LootTable> key, Consumer<LootPool> tableBuilder) {
        if (key.equals(BuiltInLootTables.SPAWN_BONUS_CHEST)) {
            LootPool bottlePool = LootPool.lootPool().add(LootItem.lootTableItem(Items.GLASS_BOTTLE)).setRolls(ContextIntProviders.binomial(5, 0.9F)).build();
            tableBuilder.accept(bottlePool);
            LootPool bowlPool = LootPool.lootPool().add(LootItem.lootTableItem(ItemInit.PURIFIED_WATER_BOWL)).setRolls(ContextIntProviders.binomial(2, 0.6F)).build();
            tableBuilder.accept(bowlPool);
        } else if (key.equals(BuiltInLootTables.VILLAGE_BUTCHER) || key.equals(BuiltInLootTables.VILLAGE_CARTOGRAPHER) || key.equals(BuiltInLootTables.VILLAGE_SHEPHERD)) {
            LootPool bowlPool = LootPool.lootPool().add(LootItem.lootTableItem(ItemInit.PURIFIED_WATER_BOWL)).setRolls(ContextIntProviders.binomial(4, 0.3F)).build();
            tableBuilder.accept(bowlPool);
            LootPool bucketPool = LootPool.lootPool().add(LootItem.lootTableItem(ItemInit.PURIFIED_BUCKET)).setRolls(ContextIntProviders.binomial(1, 0.3F)).build();
            tableBuilder.accept(bucketPool);
        }
    }

    public static InteractionResult onUseItem(Player player, Level world, InteractionHand hand) {
        if (!player.isCreative() && !player.isSpectator() && player.isShiftKeyDown() && player.getItemInHand(hand).is(Items.BOWL)) {
            HitResult hitResult = player.pick(player.blockInteractionRange(), 0.0F, true);
            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
            if (world.mayInteract(player, blockPos) && world.getFluidState(blockPos).is(FluidTags.WATER)) {
                if (world.getFluidState(blockPos).isSource()) {
                    world.playSound(null, blockPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (!world.isClientSide()) {
                        ItemStack itemStack = new ItemStack(ItemInit.WATER_BOWL);
                        if (world.getFluidState(blockPos).is(TagInit.PURIFIED_WATER)) {
                            itemStack = new ItemStack(ItemInit.PURIFIED_WATER_BOWL);
                        }
                        player.awardStat(Stats.ITEM_USED.get(Items.BOWL));
                        player.setItemInHand(hand, ItemUtils.createFilledResult(player.getItemInHand(hand), player, itemStack));
                        ThirstHelper.drainWaterSource(player, world, blockPos);
                    }
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.PASS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onUseBlock(Player player, Level world, InteractionHand hand, BlockHitResult result) {
        if (hand == InteractionHand.MAIN_HAND && !player.isCreative() && !player.isSpectator() && player.isShiftKeyDown() && player.getMainHandItem().isEmpty()) {
            HitResult hitResult = player.pick(player.blockInteractionRange(), 0.0F, true);
            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
            if (world.mayInteract(player, blockPos) && world.getFluidState(blockPos).is(FluidTags.WATER)) {
                if (world.getFluidState(blockPos).isSource() || ConfigInit.CONFIG.allow_non_flowing_water_sip) {
                    ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager();
                    if (thirstManager.isNotFull()) {
                        int drinkTime = ((PlayerAccess) player).getDrinkTime();
                        if (world.isClientSide() && drinkTime % 3 == 0)
                            player.playSound(SoundEvents.GENERIC_DRINK.value(), 0.5f, world.getRandom().nextFloat() * 0.1f + 0.9f);

                        if (drinkTime > 20) {
                            if (!world.isClientSide()) {
                                thirstManager.add(ConfigInit.CONFIG.water_souce_quench);
                                if (!world.getFluidState(blockPos).is(TagInit.PURIFIED_WATER)) {
                                    float sipThirstChance = ConfigInit.CONFIG.water_sip_thirst_chance;
                                    if (world.getBiome(blockPos).is(BiomeTags.IS_RIVER)) {
                                        sipThirstChance = sipThirstChance / 2f;
                                    }
                                    if (world.getRandom().nextFloat() <= sipThirstChance) {
                                        player.addEffect(new MobEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.water_sip_thirst_duration, 1, false, false, true));
                                    }
                                }
                                if (!ConfigInit.CONFIG.allow_non_flowing_water_sip && world.getFluidState(blockPos).isSource()) {
                                    ThirstHelper.drainWaterSource(player, world, blockPos);
                                }
                            } else {
                                world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundInit.WATER_SIP_EVENT, SoundSource.PLAYERS, 1.0F,
                                        0.9F + (world.getRandom().nextFloat() / 5F));
                            }
                            ((PlayerAccess) player).setDrinkTime(0);
                            return InteractionResult.SUCCESS;
                        }

                        ((PlayerAccess) player).setDrinkTime(drinkTime + 1);
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
}
