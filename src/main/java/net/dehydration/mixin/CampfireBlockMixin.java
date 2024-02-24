package net.dehydration.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.dehydration.init.BlockInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BlockWithEntity {
    public CampfireBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onUse", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/entity/CampfireBlockEntity;getRecipeFor(Lnet/minecraft/item/ItemStack;)Ljava/util/Optional;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onUseMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info, BlockEntity blockEntity,
            CampfireBlockEntity campfireBlockEntity, ItemStack itemStack, Optional<CampfireCookingRecipe> optional) {
        if (!world.isClient() && itemStack.getItem() instanceof PotionItem && PotionUtil.getPotion(itemStack) == Potions.WATER
                && campfireBlockEntity.addItem(player, player.getAbilities().creativeMode ? itemStack.copy() : itemStack, 1000)) {
            player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
            info.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (world.getBlockState(pos.up()).getBlock() == BlockInit.CAMPFIRE_CAULDRON_BLOCK) {
            world.breakBlock(pos.up(), !player.isCreative());
        }
    }

}
