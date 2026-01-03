package net.dehydration.init;

import net.dehydration.access.PlayerAccess;
import net.dehydration.access.ServerPlayerAccess;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.network.ThirstServerPacket;
import net.dehydration.thirst.ThirstManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

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
            if (key.equals(LootTables.SPAWN_BONUS_CHEST)) {
                LootPool pool = LootPool.builder().with(ItemEntry.builder(Items.GLASS_BOTTLE).build()).rolls(BinomialLootNumberProvider.create(5, 0.9F)).build();
                tableBuilder.pool(pool);
            }
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!player.isCreative() && !player.isSpectator() && player.isSneaking() && player.getStackInHand(hand).isOf(Items.BOWL)) {
                HitResult hitResult = player.raycast(player.getBlockInteractionRange(), 0.0F, true);
                BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                if (world.canPlayerModifyAt(player, blockPos) && world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                    if (world.getFluidState(blockPos).isStill()) {
                        world.playSound(null, blockPos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        if (!world.isClient()) {
                            ItemStack itemStack = new ItemStack(ItemInit.WATER_BOWL);
                            if (world.getFluidState(blockPos).isIn(TagInit.PURIFIED_WATER)) {
                                itemStack = new ItemStack(ItemInit.PURIFIED_WATER_BOWL);
                            }
                            player.setStackInHand(hand, ItemUsage.exchangeStack(player.getStackInHand(hand), player, itemStack));
                            player.incrementStat(Stats.USED.getOrCreateStat(player.getStackInHand(hand).getItem()));
                            if (world.getBlockState(blockPos).contains(Properties.WATERLOGGED)) {
                                world.setBlockState(blockPos, world.getBlockState(blockPos).with(Properties.WATERLOGGED, false));
                            } else {
                                world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
                            }
                        }
                        return TypedActionResult.success(player.getStackInHand(hand), true);
                    } else {
                        return TypedActionResult.pass(player.getStackInHand(hand));
                    }
                }
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });
        
        UseBlockCallback.EVENT.register((player, world, hand, result) -> {
            if (!player.isCreative() && !player.isSpectator() && player.isSneaking() && player.getMainHandStack().isEmpty()) {
                HitResult hitResult = player.raycast(player.getBlockInteractionRange(), 0.0F, true);
                BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                if (world.canPlayerModifyAt(player, blockPos) && world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                    if (world.getFluidState(blockPos).isStill() || ConfigInit.CONFIG.allow_non_flowing_water_sip) {
                        ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager();
                        if (thirstManager.isNotFull()) {
                            int drinkTime = ((PlayerAccess) player).getDrinkTime();
                            if (world.isClient() && drinkTime % 3 == 0)
                                player.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5f, world.getRandom().nextFloat() * 0.1f + 0.9f);

                            if (drinkTime > 20) {
                                if (!world.isClient()) {
                                    thirstManager.add(ConfigInit.CONFIG.water_souce_quench);
                                    if (!world.getFluidState(blockPos).isIn(TagInit.PURIFIED_WATER)) {
                                        float sipThirstChance = ConfigInit.CONFIG.water_sip_thirst_chance;
                                        if (world.getBiome(blockPos).isIn(BiomeTags.IS_RIVER)) {
                                            sipThirstChance = sipThirstChance / 2f;
                                        }
                                        if (world.getRandom().nextFloat() <= sipThirstChance) {
                                            player.addStatusEffect(new StatusEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.water_sip_thirst_duration, 1, false, false, true));
                                        }
                                    }
                                    if (!ConfigInit.CONFIG.allow_non_flowing_water_sip && world.getFluidState(blockPos).isStill()) {
                                        if (world.getBlockState(blockPos).contains(Properties.WATERLOGGED)) {
                                            world.setBlockState(blockPos, world.getBlockState(blockPos).with(Properties.WATERLOGGED, false));
                                        } else {
                                            world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
                                        }
                                    }
                                } else {
                                    world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundInit.WATER_SIP_EVENT, SoundCategory.PLAYERS, 1.0F,
                                            0.9F + (world.getRandom().nextFloat() / 5F));
                                }
                                ((PlayerAccess) player).setDrinkTime(0);
                                return ActionResult.SUCCESS;
                            }

                            ((PlayerAccess) player).setDrinkTime(drinkTime + 1);
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });

        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder) -> {
            builder.registerPotionRecipe(Potions.WATER, Items.CHARCOAL, ItemInit.PURIFIED_WATER);
            builder.registerPotionRecipe(Potions.WATER, Items.KELP, ItemInit.PURIFIED_WATER);
            builder.registerPotionRecipe(ItemInit.PURIFIED_WATER, Items.GHAST_TEAR, ItemInit.HYDRATION);
        });
    }
}
