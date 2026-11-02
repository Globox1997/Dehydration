package net.dehydration.mixin.compat;

import eu.midnightdust.puddles.block.PuddleBlock;
import net.dehydration.init.ItemInit;
import net.dehydration.init.SoundInit;
import net.dehydration.item.LeatherFlask;
import net.dehydration.item.component.FlaskComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PuddleBlock.class)
public class PuddleBlockMixin {

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void onUseWithItemMixin(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<ItemInteractionResult> info) {
        if (stack.getItem() instanceof LeatherFlask leatherFlask) {
            FlaskComponent flaskComponent = stack.getOrDefault(ItemInit.FLASK_DATA, FlaskComponent.DEFAULT);
            if (flaskComponent.fillLevel() < 2 + leatherFlask.getExtraFillLevel()) {
                if (!world.isClientSide()) {
                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    int qualityLevel = flaskComponent.fillLevel() == 0 || flaskComponent.qualityLevel() == 2 ? 2 : 1;
                    LeatherFlask.fillFlask(stack, 2, qualityLevel);
                }
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundInit.FILL_FLASK_EVENT, SoundSource.NEUTRAL, 1.0F, 1.0F);
                info.setReturnValue(ItemInteractionResult.sidedSuccess(world.isClientSide()));
            }
        }
    }
}
