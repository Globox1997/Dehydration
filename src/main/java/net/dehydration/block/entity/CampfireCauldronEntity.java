package net.dehydration.block.entity;

import net.dehydration.block.CampfireCauldronBlock;
import net.dehydration.init.BlockInit;
import net.dehydration.init.ConfigInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CampfireCauldronEntity extends BlockEntity {
    public boolean isBoiled;
    private int ticker;

    public CampfireCauldronEntity(BlockPos pos, BlockState state) {
        super(BlockInit.CAMPFIRE_CAULDRON_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag, WrapperLookup registryLookup) {
        super.readNbt(tag, registryLookup);
        this.isBoiled = tag.getBoolean("Boiled");
        this.ticker = tag.getInt("BoilingTicks");
        if (this.world != null && this.world.isClient()) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public void writeNbt(NbtCompound tag, WrapperLookup registryLookup) {
        super.writeNbt(tag, registryLookup);
        tag.putBoolean("Boiled", isBoiled);
        tag.putInt("BoilingTicks", ticker);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, CampfireCauldronEntity blockEntity) {
        blockEntity.update();
    }

    public void update() {
        if (this.world == null) {
            return;
        }
        if (this.getCachedState().get(CampfireCauldronBlock.LEVEL) <= 0) {
            if (this.isBoiled || this.ticker != 0) {
                this.isBoiled = false;
                this.ticker = 0;
                this.markDirty();
            }
            return;
        }
        CampfireCauldronBlock campfireCauldronBlock = (CampfireCauldronBlock) this.getCachedState().getBlock();
        if (!this.isBoiled && campfireCauldronBlock.isFireBurning(world, pos)) {
            this.ticker++;
            if (this.ticker >= ConfigInit.CONFIG.water_boiling_time) {
                this.isBoiled = true;
                this.ticker = 0;
                this.markDirty();
            }
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        sendUpdate();
    }

    private void sendUpdate() {
        if (this.world != null) {
            BlockState state = this.world.getBlockState(this.pos);
            (this.world).updateListeners(this.pos, state, state, 3);
        }
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    public void onFillingCauldron() {
        this.isBoiled = false;
        this.ticker = 0;
        this.markDirty();
    }

}
