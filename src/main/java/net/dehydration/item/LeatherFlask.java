package net.dehydration.item;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.block.CopperLeveledCauldronBlock;
import net.dehydration.block.entity.CampfireCauldronEntity;
import net.dehydration.init.*;
import net.dehydration.item.component.FlaskComponent;
import net.dehydration.misc.ThirstTooltipData;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import java.util.List;
import java.util.Optional;

// Thanks to Pois1x for the texture
public class LeatherFlask extends Item {

    private final int addition;

    public LeatherFlask(int waterAddition, Properties settings) {
        super(settings);
        this.addition = waterAddition;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);
        FlaskComponent flaskComponent = itemStack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);

        boolean isEmptyCauldron = state.is(Blocks.CAULDRON);
        boolean isWaterCauldron = state.is(Blocks.WATER_CAULDRON);
        boolean isEmptyCopperCauldron = state.is(BlockInit.COPPER_CAULDRON_BLOCK);
        boolean isWaterCopperCauldron = state.is(BlockInit.COPPER_WATER_CAULDRON_BLOCK) || state.is(BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK);
        boolean isCampfireCauldron = state.getBlock() instanceof CampfireCauldronBlock;

        if (isEmptyCauldron || isWaterCauldron || isEmptyCopperCauldron || isWaterCopperCauldron || isCampfireCauldron) {

            // Empty flask
            if (player.isShiftKeyDown()) {
                if (flaskComponent.fillLevel() > 0) {
                    if (!player.level().isClientSide()) {
                        if (isEmptyCauldron || isWaterCauldron) {
                            if (isWaterCauldron) {
                                if (((LayeredCauldronBlock) state.getBlock()).isFull(state)) {
                                    return super.useOn(context);
                                }
                                player.level().setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
                            } else {
                                player.level().setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState());
                                player.level().gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                            }
                        } else if (isEmptyCopperCauldron || isWaterCopperCauldron) {
                            if (isWaterCopperCauldron) {
                                if (((CopperLeveledCauldronBlock) state.getBlock()).isFull(state)) {
                                    return super.useOn(context);
                                }
                                if (flaskComponent.qualityLevel() != 0) {
                                    player.level().setBlockAndUpdate(pos,
                                            BlockInit.COPPER_WATER_CAULDRON_BLOCK.defaultBlockState().setValue(CopperLeveledCauldronBlock.LEVEL, state.getValue(CopperLeveledCauldronBlock.LEVEL) + 1));
                                } else {
                                    player.level().setBlockAndUpdate(pos, state.cycle(CopperLeveledCauldronBlock.LEVEL));
                                }
                            } else {
                                if (flaskComponent.qualityLevel() == 0) {
                                    player.level().setBlockAndUpdate(pos, BlockInit.COPPER_PURIFIED_WATER_CAULDRON_BLOCK.defaultBlockState());
                                } else {
                                    player.level().setBlockAndUpdate(pos, BlockInit.COPPER_WATER_CAULDRON_BLOCK.defaultBlockState());
                                }
                                player.level().gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                            }
                        } else {
                            CampfireCauldronBlock campfireCauldronBlock = (CampfireCauldronBlock) state.getBlock();
                            if (campfireCauldronBlock.isFull(state)) {
                                return super.useOn(context);
                            }
                            if ((flaskComponent.qualityLevel() != 0 || !campfireCauldronBlock.isPurifiedWater(player.level(), pos))
                                    && player.level().getBlockEntity(pos) instanceof CampfireCauldronEntity campfireCauldronEntity) {
                                campfireCauldronEntity.onFillingCauldron();
                            }
                            player.level().setBlockAndUpdate(pos, state.cycle(CampfireCauldronBlock.LEVEL));
                        }
                        player.level().playSound(null, pos, SoundInit.EMPTY_FLASK_EVENT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        player.awardStat(Stats.USE_CAULDRON);

                        if (flaskComponent.fillLevel() > 0) {
                            itemStack.set(ItemInit.FLASK_DATA, new FlaskComponent(flaskComponent.fillLevel() - 1, flaskComponent.qualityLevel()));
                        }
                    }
                    return InteractionResult.sidedSuccess(player.level().isClientSide());
                }
            } else if (isWaterCauldron && state.getValue(LayeredCauldronBlock.LEVEL) > 0 && flaskComponent.fillLevel() < 2 + this.addition) {
                // Fill up flask
                if (!player.level().isClientSide()) {
                    player.level().playSound(null, pos, SoundInit.FILL_FLASK_EVENT, SoundSource.BLOCKS, 1.0F, 1.0F);
                    player.awardStat(Stats.USE_CAULDRON);
                    LayeredCauldronBlock.lowerFillLevel(state, player.level(), pos);
                    itemStack.set(ItemInit.FLASK_DATA, new FlaskComponent(flaskComponent.fillLevel() + 1, 2));
                }
                return InteractionResult.sidedSuccess(player.level().isClientSide());
            }
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        FlaskComponent flaskComponent = itemStack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        BlockHitResult hitResult = getPlayerPOVHitResult(world, user, ClipContext.Fluid.SOURCE_ONLY);
        BlockPos blockPos = hitResult.getBlockPos();


        if (hitResult.getType() == HitResult.Type.BLOCK && world.mayInteract(user, blockPos) && world.getFluidState(blockPos).is(FluidTags.WATER)) {
            if (user.isShiftKeyDown() && flaskComponent.fillLevel() != 0) {
                itemStack.set(ItemInit.FLASK_DATA, new FlaskComponent(0, flaskComponent.qualityLevel()));
                world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundInit.EMPTY_FLASK_EVENT, SoundSource.NEUTRAL, 1.0F, 1.0F);
                return InteractionResultHolder.consume(itemStack);
            }
            if (flaskComponent.fillLevel() < 2 + addition) {
                int fillLevel = 2 + addition;
                int waterPurity = 2;

                boolean isEmpty = flaskComponent.fillLevel() == 0;
                boolean isDirtyWater = flaskComponent.qualityLevel() == 2;
                if (!isEmpty && !isDirtyWater) {
                    waterPurity = 1;
                }

                boolean riverWater = world.getBiome(blockPos).is(BiomeTags.IS_RIVER);
                if (riverWater && (isEmpty || (!isEmpty && !isDirtyWater))) {
                    waterPurity = 0;
                }

                world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundInit.FILL_FLASK_EVENT, SoundSource.NEUTRAL, 1.0F, 1.0F);
                itemStack.set(ItemInit.FLASK_DATA, new FlaskComponent(fillLevel, waterPurity));
                return InteractionResultHolder.consume(itemStack);
            }
        }
        if (flaskComponent.fillLevel() == 0) {
            return InteractionResultHolder.pass(itemStack);
        } else {
            return ItemUtils.startUsingInstantly(world, user, hand);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        if (user instanceof Player playerEntity) {
            if (user instanceof ServerPlayer serverPlayerEntity) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            }
            playerEntity.awardStat(Stats.ITEM_USED.get(this));
            if (!playerEntity.isCreative()) {
                stack.set(ItemInit.FLASK_DATA, new FlaskComponent(flaskComponent.fillLevel() - 1, flaskComponent.qualityLevel()));
                if (!world.isClientSide()) {
                    ThirstManager thirstManager = ((ThirstManagerAccess) user).getThirstManager();
                    thirstManager.add(ConfigInit.CONFIG.flask_thirst_quench);
                    if (flaskComponent.qualityLevel() == 2 && world.random.nextFloat() <= ConfigInit.CONFIG.flask_dirty_thirst_chance) {
                        user.addEffect(new MobEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.flask_dirty_thirst_duration, 1, false, false, true));
                    } else if (flaskComponent.qualityLevel() == 1 && world.random.nextFloat() <= ConfigInit.CONFIG.flask_dirty_thirst_chance * 0.5F) {
                        user.addEffect(new MobEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.flask_dirty_thirst_duration, 0, false, false, true));
                    }
                }
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        if (flaskComponent.fillLevel() > 0) {
            return UseAnim.DRINK;
        } else {
            return UseAnim.NONE;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        if (stack.get(ItemInit.FLASK_DATA) != null) {
            tooltip.add(Component.translatable("item.dehydration.leather_flask.tooltip", flaskComponent.fillLevel(), addition + 2).withStyle(ChatFormatting.GRAY));
            if (flaskComponent.fillLevel() > 0) {
                String string = "dirty";
                if (flaskComponent.qualityLevel() == 1) {
                    string = "impurified";
                } else if (flaskComponent.qualityLevel() == 0) {
                    string = "purified";
                }
                tooltip.add(Component.translatable("item.dehydration.leather_flask.tooltip3." + string));
            }
        } else {
            tooltip.add(Component.translatable("item.dehydration.leather_flask.tooltip2", addition + 2).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, type);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (ConfigInit.CONFIG.thirst_preview) {
            FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
            if (flaskComponent.fillLevel() == 0) {
                return Optional.empty();
            }
            return Optional.of(new ThirstTooltipData(flaskComponent.qualityLevel(), flaskComponent.fillLevel() * ConfigInit.CONFIG.flask_thirst_quench));
        }
        return super.getTooltipImage(stack);
    }

    public int getExtraFillLevel() {
        return this.addition;
    }

    public static void fillFlask(ItemStack itemStack, int quench) {
        FlaskComponent flaskComponent = itemStack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        fillFlask(itemStack, quench, flaskComponent.qualityLevel());
    }

    public static void fillFlask(ItemStack itemStack, int quench, int qualityLevel) {
        FlaskComponent flaskComponent = itemStack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
        int fillQuench = flaskComponent.fillLevel() + quench;
        int addition = ((LeatherFlask) itemStack.getItem()).addition;
        itemStack.set(ItemInit.FLASK_DATA, new FlaskComponent(fillQuench > 2 + addition ? 2 + addition : fillQuench, qualityLevel));
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
