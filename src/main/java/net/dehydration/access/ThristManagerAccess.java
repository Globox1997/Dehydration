package net.dehydration.access;

import net.dehydration.thirst.ThirstManager;
import net.minecraft.entity.player.PlayerEntity;

public interface ThristManagerAccess {

    public ThirstManager getThirstManager(PlayerEntity player);

}
