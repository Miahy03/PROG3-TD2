
package org.td.service;

import org.td.config.DBConnection;
import org.td.entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection dbConnection = new DBConnection();

    public List<Dish> getAllDishes() throws SQLException {
        List<Dish> dishes = new ArrayList<>();

        String sql = "SELECT d.id, d.name, d.price, d.type, c.category FROM dish d JOIN category c ON d.category_id = c.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                dishes.add(new Dish(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        DishTypeEnum.valueOf(rs.getString("type")),
                        CategoryEnum.valueOf(rs.getString("category"))
                ));
            }
        }
        return dishes;
    }
}
