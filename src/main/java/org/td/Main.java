
package org.td;

import org.td.entity.Dish;
import org.td.service.DishDataRetriever;

public class Main {

    public static void main(String[] args) {

        DishDataRetriever dao = new DishDataRetriever();

        Dish dish = dao.findDishById(1);
        System.out.println("Plat : " + dish.getName());

        try {
            System.out.println("Marge : " + dish.getGrossMargin());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        Dish newDish = new Dish(null, "Plat test", null, 1800.0);
        dao.saveDish(newDish);
        System.out.println("Plat sauvegardé");
    }
}
