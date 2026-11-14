package net.dehydration.neoforge.platform;

import net.dehydration.init.FluidInit;
import net.dehydration.neoforge.fluid.NeoForgePurifiedWaterFluid;
import net.dehydration.neoforge.transfer.FluidUnits;
import net.dehydration.platform.services.IFluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class NeoForgeFluidHelper implements IFluidHelper {

    @Override
    public FlowingFluid createPurifiedWaterFluid(boolean source) {
        return source ? new NeoForgePurifiedWaterFluid.Still() : new NeoForgePurifiedWaterFluid.Flowing();
    }

    @Override
    public LiquidBlock createPurifiedWaterBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
        return new LiquidBlock(fluid, properties) {
            @Override
            public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndLightGetter level, BlockPos pos, FluidState fluidState) {
                return true;
            }
        };
    }

    @Override
    public boolean interactWithFluidStorage(Level world, BlockPos pos, Direction side, Player player, InteractionHand hand) {
        return FluidUtil.interactWithFluidHandler(player, hand, world, pos, side, null);
    }

    @Override
    public boolean canInsertFluid(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        ResourceHandler<FluidResource> handler = ItemAccess.forStack(stack.copy()).getCapability(Capabilities.Fluid.ITEM);
        if (handler == null) {
            return false;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            return handler.insert(FluidResource.of(FluidInit.PURIFIED_WATER), FluidUnits.BUCKET, transaction) > 0;
        }
    }

    @Override
    public boolean insertPurifiedWater(Level world, BlockPos pos, int bottles) {
        ResourceHandler<ItemResource> itemHandler = world.getCapability(Capabilities.Item.BLOCK, pos, null);
        if (itemHandler == null) {
            return false;
        }
        ResourceHandler<FluidResource> fluidHandler = ItemAccess.forHandlerIndex(itemHandler, 0).getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandler == null) {
            return false;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            if (fluidHandler.insert(FluidResource.of(FluidInit.PURIFIED_WATER), bottles * FluidUnits.BOTTLE, transaction) > 0) {
                transaction.commit();
                return true;
            }
        }
        return false;
    }

}
