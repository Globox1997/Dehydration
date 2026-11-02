package net.dehydration.item;

import net.minecraft.world.InteractionResult;
import java.util.Optional;

import net.dehydration.init.ConfigInit;
import net.dehydration.init.EffectInit;
import net.dehydration.misc.ThirstTooltipData;
import net.dehydration.util.ThirstHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class WaterBowlItem extends Item {

    private final boolean hasThirstChance;

    public WaterBowlItem(Properties settings, boolean hasThirstChance) {
        super(settings);
        this.hasThirstChance = hasThirstChance;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        ItemStack itemStack = super.finishUsingItem(stack, world, user);

        if (user instanceof Player playerEntity) {
            if (!world.isClientSide() && this.hasThirstChance && world.getRandom().nextFloat() < ConfigInit.CONFIG.water_bowl_thirst_chance) {
                user.addEffect(new MobEffectInstance(EffectInit.THIRST, ConfigInit.CONFIG.potion_bad_thirst_duration / 2, 0, false, false, true));
            }

            if (playerEntity.isCreative()) {
                return itemStack;
            }
        }
        return new ItemStack(Items.BOWL);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        BlockHitResult hitResult = getPlayerPOVHitResult(world, user, ClipContext.Fluid.SOURCE_ONLY);
        BlockPos blockPos = hitResult.getBlockPos();

        FluidState fluidState = world.getFluidState(blockPos);
        if (hitResult.getType() == HitResult.Type.BLOCK && world.mayInteract(user, blockPos) && fluidState.is(FluidTags.WATER) && user.isShiftKeyDown()) {
            world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.BUCKET_EMPTY, SoundSource.NEUTRAL, 1.0F, 1.0F);
            return InteractionResult.CONSUME.heldItemTransformedTo(new ItemStack(Items.BOWL));
        }

        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (ConfigInit.CONFIG.thirst_preview) {
            int thirstQuench = ThirstHelper.getThirstQuench(stack, true);
            return Optional.of(new ThirstTooltipData(this.hasThirstChance ? 2 : 0, thirstQuench));
        } else {
            return super.getTooltipImage(stack);
        }
    }

}
