package net.dehydration.mixin;

import net.minecraft.block.LeveledCauldronBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.init.SoundInit;
import net.dehydration.item.Leather_Flask;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(AbstractCauldronBlock.class)
public class CauldronBlockMixin {

    @Inject(method = "onUse", at = @At(value = "HEAD"), cancellable = true)
    public void onUseMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() instanceof Leather_Flask) {
            NbtCompound tags = itemStack.getNbt();
            Leather_Flask item = (Leather_Flask) itemStack.getItem();
            if (itemStack.hasNbt() && tags.getInt("leather_flask") < 2 + item.addition) {
                if (state.get(LeveledCauldronBlock.LEVEL) > 0 && !world.isClient) {
                    world.playSound((PlayerEntity) null, pos, SoundInit.FILL_FLASK_EVENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                    player.incrementStat(Stats.USE_CAULDRON);
                    tags.putInt("leather_flask", tags.getInt("leather_flask") + 1);
                    tags.putBoolean("purified_water", false);
                    itemStack.setNbt(tags);
                }
                info.setReturnValue(ActionResult.success(world.isClient));
            }
        }
    }

}