package net.dehydration.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;

public class HydrationTemplate {

    private final int hydration;
    private final List<Item> items = new ArrayList<Item>();

    public HydrationTemplate(int hydration, List<Item> items) {
        this.hydration = hydration;
        this.items.addAll(items);
    }

    public int getHydration() {
        return hydration;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public boolean containsItem(Item item) {
        return this.items.contains(item);
    }

}
