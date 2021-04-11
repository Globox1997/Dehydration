package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.dehydration.init.BlockInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BlockWithEntity {
    public CampfireBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (world.getBlockState(pos.up()).getBlock() == BlockInit.CAMPFIRE_CAULDRON_BLOCK) {
            world.breakBlock(pos.up(), !player.isCreative());
        }
    }

}
