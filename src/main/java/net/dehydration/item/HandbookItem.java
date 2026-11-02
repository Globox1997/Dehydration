package net.dehydration.item;

import java.util.List;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.PatchouliAPI;

public class HandbookItem extends Item {

    private final boolean isPatchouliLoaded = FabricLoader.getInstance().isModLoaded("patchouli");

    public HandbookItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!world.isClientSide() && isPatchouliLoaded) {
            PatchouliAPI.get().openBookGUI((ServerPlayer) user, ResourceLocation.fromNamespaceAndPath("dehydration", "dehydration"));
            return InteractionResultHolder.success(user.getItemInHand(hand));
        }
        return InteractionResultHolder.fail(user.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        if (!isPatchouliLoaded) {
            tooltip.add(Component.translatable("item.dehydration.patchouli_book.tooltip"));
        }
        super.appendHoverText(stack, context, tooltip, type);
    }

}
