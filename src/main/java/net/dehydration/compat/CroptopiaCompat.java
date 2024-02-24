package net.dehydration.compat;

import com.epherical.croptopia.event.DrinkEvent;

import net.dehydration.DehydrationMain;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.TagInit;
import net.dehydration.thirst.ThirstManager;

public class CroptopiaCompat {

    public static void init() {
        DrinkEvent.DRINK.register((stack, player) -> {
            ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager();
            int thirst = 0;
            if (stack.isIn(TagInit.HYDRATING_STEW)) {
                thirst = ConfigInit.CONFIG.stew_thirst_quench;
            }
            if (stack.isIn(TagInit.HYDRATING_FOOD)) {
                thirst = ConfigInit.CONFIG.food_thirst_quench;
            }
            if (stack.isIn(TagInit.HYDRATING_DRINKS)) {
                thirst = ConfigInit.CONFIG.drinks_thirst_quench;
            }
            if (stack.isIn(TagInit.STRONGER_HYDRATING_STEW)) {
                thirst = ConfigInit.CONFIG.stronger_stew_thirst_quench;
            }
            if (stack.isIn(TagInit.STRONGER_HYDRATING_FOOD)) {
                thirst = ConfigInit.CONFIG.stronger_food_thirst_quench;
            }
            if (stack.isIn(TagInit.STRONGER_HYDRATING_DRINKS)) {
                thirst = ConfigInit.CONFIG.stronger_drinks_thirst_quench;
            }
            for (int i = 0; i < DehydrationMain.HYDRATION_TEMPLATES.size(); i++) {
                if (DehydrationMain.HYDRATION_TEMPLATES.get(i).containsItem(stack.getItem())) {
                    thirst = DehydrationMain.HYDRATION_TEMPLATES.get(i).getHydration();
                    break;
                }
            }
            if (thirst > 0) {
                thirstManager.add(thirst);
            }
        });
    }

}
