package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.dehydration.init.SoundInit;
import net.dehydration.item.Leather_Flask;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(CauldronBlock.class)
public class CauldronBlockMixin {

    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void onUseMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit, CallbackInfoReturnable<ActionResult> info, ItemStack itemStack, int i) {
        if (itemStack.getItem() instanceof Leather_Flask) {
            CompoundTag tags = itemStack.getTag();
            Leather_Flask item = (Leather_Flask) itemStack.getItem();
            if (itemStack.hasTag() && tags.getInt("leather_flask") < 2 + item.addition) {
                if (i > 0 && !world.isClient) {
                    world.playSound((PlayerEntity) null, pos, SoundInit.FILL_FLASK_EVENT, SoundCategory.BLOCKS, 1.0F,
                            1.0F);
                    this.setLevel(world, pos, state, i - 1);
                    player.incrementStat(Stats.USE_CAULDRON);
                    tags.putInt("leather_flask", tags.getInt("leather_flask") + 1);
                    tags.putBoolean("purified_water", false);
                    itemStack.setTag(tags);
                }
                info.setReturnValue(ActionResult.success(world.isClient));
            }
        }
    }

    @Shadow
    public void setLevel(World world, BlockPos pos, BlockState state, int level) {
    }

}
