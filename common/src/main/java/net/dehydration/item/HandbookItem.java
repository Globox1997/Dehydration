package net.dehydration.item;

import net.minecraft.world.InteractionResult;
import java.util.List;

import net.dehydration.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.PatchouliAPI;

public class HandbookItem extends Item {

    private final boolean isPatchouliLoaded = Services.PLATFORM.isModLoaded("patchouli");

    public HandbookItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (!world.isClientSide() && isPatchouliLoaded) {
            PatchouliAPI.get().openBookGUI((ServerPlayer) user, Identifier.fromNamespaceAndPath("dehydration", "dehydration"));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag type) {
        if (!isPatchouliLoaded) {
            tooltip.accept(Component.translatable("item.dehydration.patchouli_book.tooltip"));
        }
        super.appendHoverText(stack, context, displayComponent, tooltip, type);
    }

}
