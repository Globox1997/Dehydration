package net.dehydration.compat;

import com.epherical.croptopia.event.DrinkEvent;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.TagInit;
import net.dehydration.thirst.ThirstManager;

public class CroptopiaCompat {

    public static void init() {
        DrinkEvent.DRINK.register((stack, player) -> {
            ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager(player);
            int thirst = 0;
            if (stack.isIn(TagInit.HYDRATING_STEW))
                thirst = ConfigInit.CONFIG.stew_thirst_quench;
            if (stack.isIn(TagInit.HYDRATING_FOOD))
                thirst = ConfigInit.CONFIG.food_thirst_quench;
            if (stack.isIn(TagInit.HYDRATING_DRINKS))
                thirst = ConfigInit.CONFIG.drinks_thirst_quench;
            if (stack.isIn(TagInit.STRONGER_HYDRATING_STEW))
                thirst = ConfigInit.CONFIG.stronger_stew_thirst_quench;
            if (stack.isIn(TagInit.STRONGER_HYDRATING_FOOD))
                thirst = ConfigInit.CONFIG.stronger_food_thirst_quench;
            if (stack.isIn(TagInit.STRONGER_HYDRATING_DRINKS))
                thirst = ConfigInit.CONFIG.stronger_drinks_thirst_quench;
            thirstManager.add(thirst);
        });
    }

}
