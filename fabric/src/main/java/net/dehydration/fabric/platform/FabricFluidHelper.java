package net.dehydration.fabric.platform;

import net.dehydration.fluid.PurifiedWaterFluid;
import net.dehydration.init.FluidInit;
import net.dehydration.platform.services.IFluidHelper;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public class FabricFluidHelper implements IFluidHelper {

    @Override
    public FlowingFluid createPurifiedWaterFluid(boolean source) {
        return source ? new PurifiedWaterFluid.Still() : new PurifiedWaterFluid.Flowing();
    }

    @Override
    public LiquidBlock createPurifiedWaterBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
        return new LiquidBlock(fluid, properties) {
        };
    }

    @Override
    public boolean interactWithFluidStorage(Level world, BlockPos pos, Direction side, Player player, InteractionHand hand) {
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, pos, side);
        return storage != null && FluidStorageUtil.interactWithFluidStorage(storage, player, hand);
    }

    @Override
    public boolean canInsertFluid(ItemStack stack) {
        Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
        return storage != null && storage.supportsInsertion();
    }

    @Override
    public boolean insertPurifiedWater(Level world, BlockPos pos, int bottles) {
        Storage<FluidVariant> fluidStorage = null;
        Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(world, pos, null);
        if (itemStorage instanceof SingleSlotStorage<ItemVariant> singleSlotStorage) {
            fluidStorage = ContainerItemContext.ofSingleSlot(singleSlotStorage).find(FluidStorage.ITEM);
        }
        if (fluidStorage != null && fluidStorage.supportsInsertion()) {
            long amount = bottles * FluidConstants.BOTTLE;
            try (Transaction transaction = Transaction.openOuter()) {
                if (fluidStorage.insert(FluidVariant.of(FluidInit.PURIFIED_WATER), amount, transaction) > 0) {
                    transaction.commit();
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

}
