package net.dehydration.compat;

import me.thonk.croptopia.event.DrinkEvent;
import net.dehydration.access.ThristManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.TagInit;
import net.dehydration.thirst.ThirstManager;

public class CroptopiaCompat {

    public static void init() {
        DrinkEvent.DRINK.register((stack, player) -> {
            ThirstManager thirstManager = ((ThristManagerAccess) player).getThirstManager(player);
            int thirst = 0;
            if (stack.getItem().isIn(TagInit.HYDRATING_STEW)) {
                thirst = ConfigInit.CONFIG.stew_thirst_quench;
            }
            if (stack.getItem().isIn(TagInit.HYDRATING_FOOD)) {
                thirst = ConfigInit.CONFIG.food_thirst_quench;
            }
            if (stack.getItem().isIn(TagInit.HYDRATING_DRINKS)) {
                thirst = ConfigInit.CONFIG.potion_thirst_quench;
            }
            thirstManager.add(thirst);
        });
    }

}
