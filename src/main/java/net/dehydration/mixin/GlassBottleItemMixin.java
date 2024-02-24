package net.dehydration.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.dehydration.init.ConfigInit;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(GlassBottleItem.class)
public class GlassBottleItemMixin {

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void useMixin(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info, List<AreaEffectCloudEntity> list, ItemStack itemStack,
            BlockHitResult blockHitResult, BlockPos blockPos) {
        if (!world.isClient() && ConfigInit.CONFIG.bottle_consumes_source_block)
            if (world.getBlockState(blockPos).contains(Properties.WATERLOGGED)) {
                world.setBlockState(blockPos, world.getBlockState(blockPos).with(Properties.WATERLOGGED, false));
            } else {
                world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
            }
    }
}
