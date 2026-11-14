package net.dehydration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;

@Mixin(CauldronInteraction.Dispatcher.class)
public interface CauldronDispatcherAccessor {

    @Invoker("put")
    void invokePut(Item item, CauldronInteraction interaction);
}
