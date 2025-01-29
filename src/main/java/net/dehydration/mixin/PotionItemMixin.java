package net.dehydration.mixin;

import net.dehydration.DehydrationMain;
import net.dehydration.init.ConfigInit;
import net.dehydration.misc.ThirstTooltipData;
import net.dehydration.util.ThirstHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin extends Item {

    public PotionItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void useMixin(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
        BlockHitResult hitResult = Item.raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (((HitResult) hitResult).getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = hitResult.getBlockPos();
            if (world.canPlayerModifyAt(user, blockPos) && world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                info.setReturnValue(TypedActionResult.success(new ItemStack(Items.GLASS_BOTTLE), world.isClient()));
            }
        }
    }


    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if (ConfigInit.CONFIG.thirst_preview && !(stack.getItem() instanceof ThrowablePotionItem)) {

            int thirstQuench = 0;
            for (int i = 0; i < DehydrationMain.HYDRATION_TEMPLATES.size(); i++) {
                if (DehydrationMain.HYDRATION_TEMPLATES.get(i).containsItem(stack.getItem())) {
                    thirstQuench = DehydrationMain.HYDRATION_TEMPLATES.get(i).getHydration();
                    break;
                }
            }
            if (thirstQuench == 0) {
                thirstQuench = ConfigInit.CONFIG.potion_thirst_quench;
            }
            if (stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).potion().isPresent() && ThirstHelper.isBadPotion(stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).potion().get())) {
                return Optional.of(new ThirstTooltipData(2, thirstQuench));
            }
            return Optional.of(new ThirstTooltipData(0, thirstQuench));
        }
        return Optional.empty();
    }

}
