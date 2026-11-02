package net.dehydration.init;

import net.dehydration.access.PlayerAccess;
import net.dehydration.access.ServerPlayerAccess;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.network.ThirstServerPacket;
import net.dehydration.thirst.ThirstManager;
import net.dehydration.util.ThirstHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EventInit {

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ThirstServerPacket.writeS2CExcludedSyncPacket(handler.player, ((ThirstManagerAccess) handler.player).getThirstManager().hasThirst());
            ThirstServerPacket.writeS2CHydrationTemplateSyncPacket(handler.player);
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            ((ServerPlayerAccess) player).compatSync();
        });

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            ((ThirstManagerAccess) newPlayer).getThirstManager().setThirst(((ThirstManagerAccess) oldPlayer).getThirstManager().hasThirst());
            if (alive) {
                ((ThirstManagerAccess) newPlayer).getThirstManager().setThirstLevel(((ThirstManagerAccess) oldPlayer).getThirstManager().getThirstLevel());
                ((ServerPlayerAccess) newPlayer).setSyncedThirstLevel(-1);
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            ThirstServerPacket.writeS2CExcludedSyncPacket(newPlayer, ((ThirstManagerAccess) oldPlayer).getThirstManager().hasThirst());
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (key.equals(BuiltInLootTables.SPAWN_BONUS_CHEST)) {
                LootPool bottlePool = LootPool.lootPool().with(LootItem.lootTableItem(Items.GLASS_BOTTLE).build()).setRolls(BinomialDistributionGenerator.binomial(5, 0.9F)).build();
                tableBuilder.pool(bottlePool);
                LootPool bowlPool = LootPool.lootPool().with(LootItem.lootTableItem(ItemInit.PURIFIED_WATER_BOWL).build()).setRolls(BinomialDistributionGenerator.binomial(2, 0.6F)).build();
                tableBuilder.pool(bowlPool);
            } else if (key.equals(BuiltInLootTables.VILLAGE_BUTCHER) || key.equals(BuiltInLootTables.VILLAGE_CARTOGRAPHER) || key.equals(BuiltInLootTables.VILLAGE_SHEPHERD)) {
                LootPool bowlPool = LootPool.lootPool().with(LootItem.lootTableItem(ItemInit.PURIFIED_WATER_BOWL).build()).setRolls(BinomialDistributionGenerator.binomial(4, 0.3F)).build();
                tableBuilder.pool(bowlPool);
                LootPool bucketPool = LootPool.lootPool().with(LootItem.lootTableItem(ItemInit.PURIFIED_BUCKET).build()).setRolls(BinomialDistributionGenerator.binomial(1, 0.3F)).build();
                tableBuilder.pool(bucketPool);
            }
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
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
                        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), true);
                    } else {
                        return InteractionResultHolder.pass(player.getItemInHand(hand));
                    }
                }
            }
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        });

        UseBlockCallback.EVENT.register((player, world, hand, result) -> {
            if (hand == InteractionHand.MAIN_HAND && !player.isCreative() && !player.isSpectator() && player.isShiftKeyDown() && player.getMainHandItem().isEmpty()) {
                HitResult hitResult = player.pick(player.blockInteractionRange(), 0.0F, true);
                BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                if (world.mayInteract(player, blockPos) && world.getFluidState(blockPos).is(FluidTags.WATER)) {
                    if (world.getFluidState(blockPos).isSource() || ConfigInit.CONFIG.allow_non_flowing_water_sip) {
                        ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager();
                        if (thirstManager.isNotFull()) {
                            int drinkTime = ((PlayerAccess) player).getDrinkTime();
                            if (world.isClientSide() && drinkTime % 3 == 0)
                                player.playSound(SoundEvents.GENERIC_DRINK, 0.5f, world.getRandom().nextFloat() * 0.1f + 0.9f);

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
        });

        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder) -> {
            builder.addMix(Potions.WATER, Items.CHARCOAL, ItemInit.PURIFIED_WATER);
            builder.addMix(Potions.WATER, Items.KELP, ItemInit.PURIFIED_WATER);
            builder.addMix(ItemInit.PURIFIED_WATER, Items.GHAST_TEAR, ItemInit.HYDRATION);
        });
    }
}
