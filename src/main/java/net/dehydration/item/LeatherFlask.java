package net.dehydration.item;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.block.AbstractCopperCauldronBlock;
import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.CopperCauldronBlock;
import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.init.*;
import net.dehydration.item.component.FlaskComponent;
import net.dehydration.misc.ThirstTooltipData;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.Optional;

// Thanks to Pois1x for the texture
public class LeatherFlask extends Item {

    private final int addition;

    public LeatherFlask(int waterAddition, Settings settings) {
        super(settings);
        this.addition = waterAddition;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getStack();
        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(pos);
        FlaskComponent flaskComponent = itemStack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);

        if (state.getBlock() instanceof LeveledCauldronBlock || state.getBlock() instanceof CauldronBlock || state.getBlock() instanceof CopperCauldronBlock
                || state.getBlock() instanceof CopperLeveledCauldronBlock || state.getBlock() instanceof CampfireCauldronBlock) {

            // Empty flask
            if (player.isSneaking()) {
                if (flaskComponent.fillLevel() > 0) {
                    if (!player.getWorld().isClient()) {
                        if (state.getBlock() instanceof AbstractCauldronBlock) {
                            if (state.getBlock() instanceof LeveledCauldronBlock) {
                                if (((LeveledCauldronBlock) state.getBlock()).isFull(state)) {
                                    return super.useOnBlock(context);
                                }
                                player.getWorld().setBlockState(pos, state.cycle(LeveledCauldronBlock.LEVEL));
                            } else {
                                player.getWorld().setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState());
                                player.getWorld().emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                            }
                        } else if (state.getBlock() instanceof AbstractCopperCauldronBlock) {
                            if (state.getBlock() instanceof CopperLeveledCauldronBlock) {
                                if (((CopperLeveledCauldronBlock) state.getBlock()).isFull(state)) {
                                    return super.useOnBlock(context);
                                }
                                if (flaskComponent.qualityLevel() != 0) {
                                    player.getWorld().setBlockState(pos,
                                            BlockInit.COPPER_WATER_CAULDRON_BLOCK.getDefaultState().with(CopperLeveledCauldronBlock.LEVEL, state.get(CopperLeveledCauldronBlock.LEVEL) + 1));
                                } else {
                                    player.getWorld().setBlockState(pos, state.cycle(CopperLeveledCauldronBlock.LEVEL));
                                }
                            } else {
                                if (flaskComponent.qualityLevel() == 0) {
                                    player.getWorld().setBlockState(pos, BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK.getDefaultState());
                                } else {
                                    player.getWorld().setBlockState(pos, BlockInit.COPPER_WATER_CAULDRON_BLOCK.getDefaultState());
                                }
                                player.getWorld().emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                            }
                        } else {
                            if (((CampfireCauldronBlock) state.getBlock()).isFull(state)) {
                                return super.useOnBlock(context);
                            }
                            player.getWorld().setBlockState(pos, state.cycle(CampfireCauldronBlock.LEVEL));
                        }
                        player.getWorld().playSound(null, pos, SoundInit.EMPTY_FLASK_EVENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        player.incrementStat(Stats.USE_CAULDRON);

                        if (flaskComponent.fillLevel() > 0) {
                            itemStack.set(ItemInit.FLASK_DATA, new FlaskComponent(flaskComponent.fillLevel() - 1, flaskComponent.qualityLevel()));
                        }
                    }
                    return ActionResult.success(player.getWorld().isClient());
                }
            } else if (state.getBlock() instanceof LeveledCauldronBlock && state.get(LeveledCauldronBlock.LEVEL) > 0 && flaskComponent.fillLevel() < 2 + this.addition) {
                // Fill up flask
                if (!player.getWorld().isClient()) {
                    player.getWorld().playSound(null, pos, SoundInit.FILL_FLASK_EVENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    player.incrementStat(Stats.USE_CAULDRON);
                    LeveledCauldronBlock.decrementFluidLevel(state, player.getWorld(), pos);
                    itemStack.set(ItemInit.FLASK_DATA, new FlaskComponent(flaskComponent.fillLevel() + 1, 2));
                }
                return ActionResult.success(player.getWorld().isClient());
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        FlaskComponent flaskComponent = itemStack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        BlockHitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        BlockPos blockPos = hitResult.getBlockPos();


        if (hitResult.getType() == HitResult.Type.BLOCK && world.canPlayerModifyAt(user, blockPos) && world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
            if (user.isSneaking() && flaskComponent.fillLevel() != 0) {
                itemStack.set(ItemInit.FLASK_DATA, new FlaskComponent(0, flaskComponent.qualityLevel()));
                world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundInit.EMPTY_FLASK_EVENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                return TypedActionResult.consume(itemStack);
            }
            if (flaskComponent.fillLevel() < 2 + addition) {
                int fillLevel = 2 + addition;
                int waterPurity = 2;

                boolean isEmpty = flaskComponent.fillLevel() == 0;
                boolean isDirtyWater = flaskComponent.qualityLevel() == 2;
                if (!isEmpty && !isDirtyWater) {
                    waterPurity = 1;
                }

                boolean riverWater = world.getBiome(blockPos).isIn(BiomeTags.IS_RIVER);
                if (riverWater && (isEmpty || (!isEmpty && !isDirtyWater))) {
                    waterPurity = 0;
                }

                world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundInit.FILL_FLASK_EVENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                itemStack.set(ItemInit.FLASK_DATA, new FlaskComponent(fillLevel, waterPurity));
                return TypedActionResult.consume(itemStack);
            }
        }
        if (flaskComponent.fillLevel() == 0) {
            return TypedActionResult.pass(itemStack);
        } else {
            return ItemUsage.consumeHeldItem(world, user, hand);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        if (user instanceof PlayerEntity playerEntity) {
            if (user instanceof ServerPlayerEntity serverPlayerEntity) {
                Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            }
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.isCreative()) {
                stack.set(ItemInit.FLASK_DATA, new FlaskComponent(flaskComponent.fillLevel() - 1, flaskComponent.qualityLevel()));
                ThirstManager thirstManager = ((ThirstManagerAccess) user).getThirstManager();
                thirstManager.add(ConfigInit.CONFIG.flask_thirst_quench);
                if (!world.isClient()) {
                    if (flaskComponent.qualityLevel() == 2 && world.random.nextFloat() <= ConfigInit.CONFIG.flask_dirty_thirst_chance) {
                        user.addStatusEffect(new StatusEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.flask_dirty_thirst_duration, 1, false, false, true));
                    } else if (flaskComponent.qualityLevel() == 1 && world.random.nextFloat() <= ConfigInit.CONFIG.flask_dirty_thirst_chance * 0.5F) {
                        user.addStatusEffect(new StatusEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.flask_dirty_thirst_duration, 0, false, false, true));
                    }
                }
            }
        }

        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        if (flaskComponent.fillLevel() > 0) {
            return UseAction.DRINK;
        } else {
            return UseAction.NONE;
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        if (stack.get(ItemInit.FLASK_DATA) != null) {
            tooltip.add(Text.translatable("item.dehydration.leather_flask.tooltip", flaskComponent.fillLevel(), addition + 2).formatted(Formatting.GRAY));
            if (flaskComponent.fillLevel() > 0) {
                String string = "dirty";
                if (flaskComponent.qualityLevel() == 1) {
                    string = "impurified";
                } else if (flaskComponent.qualityLevel() == 0) {
                    string = "purified";
                }
                tooltip.add(Text.translatable("item.dehydration.leather_flask.tooltip3." + string));
            }
        } else {
            tooltip.add(Text.translatable("item.dehydration.leather_flask.tooltip2", addition + 2).formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if (ConfigInit.CONFIG.thirst_preview) {
            FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
            if (flaskComponent.fillLevel() == 0) {
                return Optional.empty();
            }
            return Optional.of(new ThirstTooltipData(flaskComponent.qualityLevel(), flaskComponent.fillLevel() * ConfigInit.CONFIG.flask_thirst_quench));
        }
        return super.getTooltipData(stack);
    }

    public int getExtraFillLevel() {
        return this.addition;
    }

    public static void fillFlask(ItemStack itemStack, int quench) {
        FlaskComponent flaskComponent = itemStack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        int fillQuench = flaskComponent.fillLevel() + quench;
        int addition = ((LeatherFlask) itemStack.getItem()).addition;
        itemStack.set(ItemInit.FLASK_DATA, new FlaskComponent(fillQuench > 2 + addition ? 2 + addition : fillQuench, flaskComponent.qualityLevel()));
    }

    public static boolean isFlaskEmpty(ItemStack stack) {
        FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        return flaskComponent.fillLevel() <= 0;
    }

    public static boolean isFlaskFull(ItemStack stack) {
        FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        return flaskComponent.fillLevel() >= ((LeatherFlask) stack.getItem()).addition + 2;
    }

    // purified_water: 0 = purified, 1 impurified, 2 dirty

}
