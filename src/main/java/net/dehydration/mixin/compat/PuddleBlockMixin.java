package net.dehydration.mixin.compat;

import eu.midnightdust.puddles.block.PuddleBlock;
import net.dehydration.init.ItemInit;
import net.dehydration.init.SoundInit;
import net.dehydration.item.LeatherFlask;
import net.dehydration.item.component.FlaskComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PuddleBlock.class)
public class PuddleBlockMixin {

    @Inject(method = "onUseWithItem", at = @At("HEAD"), cancellable = true)
    public void onUseWithItemMixin(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ItemActionResult> info) {
        if (stack.getItem() instanceof LeatherFlask leatherFlask) {
            FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
            if (flaskComponent.fillLevel() < 2 + leatherFlask.getExtraFillLevel()) {
                if (!world.isClient()) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    LeatherFlask.fillFlask(stack, 2);
                }
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundInit.FILL_FLASK_EVENT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                info.setReturnValue(ItemActionResult.success(world.isClient()));
            }
        }
    }
}
