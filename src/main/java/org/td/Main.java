package org.td;

import org.td.entity.DishOrder;
import org.td.entity.Ingredient;
import org.td.entity.Order;
import org.td.service.DataRetriever;
import org.td.entity.Dish;
import org.td.service.TableService;


import java.sql.SQLException;
import java.time.Instant;
import java.util.List;


public class Main {

    public static void main(String[] args) throws SQLException {

        /* comment out every test that would throw error before running all test to show all normal result */
        DataRetriever dr = new DataRetriever();

        //Dish dish1 = dr.findDishById(1);

        //DishOrder dishOrder = new DishOrder(2 , dish1 , 1000);
       // Order order = new Order(4 ,"ORD00004", Instant.now() , List.of(dishOrder));

      //  System.out.println(dr.saveOrder(order));
        TableService tableService = new TableService();

        Instant arrival = Instant.now();
        Instant departure = arrival.plusSeconds(3600);

        List<Integer> availableTables =
                tableService.findAvailableTables(arrival, departure);

        System.out.println("Tables disponibles : " + availableTables);


    }
}
