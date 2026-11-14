package net.dehydration.mixin;

import net.minecraft.world.InteractionResult;
import net.dehydration.init.ConfigInit;
import net.dehydration.misc.ThirstTooltipData;
import net.dehydration.util.ThirstHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin extends Item {

    public PotionItemMixin(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (!user.isShiftKeyDown()) {
            return super.use(world, user, hand);
        }
        BlockHitResult hitResult = Item.getPlayerPOVHitResult(world, user, ClipContext.Fluid.SOURCE_ONLY);
        if (((HitResult) hitResult).getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = hitResult.getBlockPos();
            if (world.mayInteract(user, blockPos) && world.getFluidState(blockPos).is(FluidTags.WATER)) {
                world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.BOTTLE_EMPTY, SoundSource.NEUTRAL, 1.0f, 1.0f);
                return InteractionResult.SUCCESS.heldItemTransformedTo(new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        return super.use(world, user, hand);
    }


    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (ConfigInit.CONFIG.thirst_preview && !(stack.getItem() instanceof ThrowablePotionItem)) {

            int thirstQuench = ThirstHelper.getThirstQuench(stack, true);
            if (ThirstHelper.isBadPotion(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion().orElse(Potions.WATER))) {
                return Optional.of(new ThirstTooltipData(2, thirstQuench));
            }
            return Optional.of(new ThirstTooltipData(0, thirstQuench));
        }
        return Optional.empty();
    }

}
