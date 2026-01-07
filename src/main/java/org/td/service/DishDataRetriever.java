
package org.td.service;

import org.td.config.DBConnection;
import org.td.entity.Dish;

import java.sql.*;

public class DishDataRetriever {

    public Dish findDishById(Integer id) {
        String sql = "SELECT * FROM dish WHERE id = ?";
        try (Connection c = new DBConnection().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Dish(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getObject("price", Double.class),
                        rs.getDouble("ingredients_cost")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Dish saveDish(Dish dish) {
        try (Connection c = new DBConnection().getConnection()) {
            if (dish.getId() == null) {
                PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO dish (name, price, ingredients_cost) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, dish.getName());
                ps.setObject(2, dish.getPrice());
                ps.setDouble(3, dish.getDishCost());
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    return new Dish(
                            keys.getInt(1),
                            dish.getName(),
                            dish.getPrice(),
                            dish.getDishCost()
                    );
                }
            } else {
                PreparedStatement ps = c.prepareStatement(
                        "UPDATE dish SET price = ? WHERE id = ?"
                );
                ps.setObject(1, dish.getPrice());
                ps.setInt(2, dish.getId());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dish;
    }
}
