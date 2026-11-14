package net.dehydration.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public interface IFluidHelper {

    FlowingFluid createPurifiedWaterFluid(boolean source);

    LiquidBlock createPurifiedWaterBlock(FlowingFluid fluid, BlockBehaviour.Properties properties);

    boolean interactWithFluidStorage(Level world, BlockPos pos, Direction side, Player player, InteractionHand hand);

    boolean canInsertFluid(ItemStack stack);

    boolean insertPurifiedWater(Level world, BlockPos pos, int bottles);

}
