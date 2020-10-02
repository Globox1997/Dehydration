package net.dehydration.init;

import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.item.Items;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

public class LootInit {

  public static void init() {
    LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
      if (id.equals(new Identifier(LootTables.SPAWN_BONUS_CHEST.toString()))) {
        FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(new BinomialLootTableRange(5, 0.9F))
            .with(ItemEntry.builder(Items.GLASS_BOTTLE));
        supplier.pool(poolBuilder);
      }
    });

  }

}
