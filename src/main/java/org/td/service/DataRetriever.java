package org.td.service;

import org.td.config.DBConnection;
import org.td.entity.*;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataRetriever {

    // =====================================================
    // DISH
    // =====================================================
    public Dish findDishById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                    select dish.id as dish_id,
                           dish.name as dish_name,
                           dish_type,
                           dish.selling_price as dish_price
                    from dish
                    where dish.id = ?;
                    """
            );
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                dish.setPrice(resultSet.getObject("dish_price") == null
                        ? null
                        : resultSet.getDouble("dish_price"));

                dish.setDishIngredientList(
                        findDishIngredientById(dish.getId())
                );
                return dish;
            }
            throw new RuntimeException("Dish not found " + id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    // =====================================================
    // INGREDIENT
    // =====================================================
    public Ingredient findIngredientById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                    select id, name, price, category
                    from ingredient
                    where id = ?;
                    """
            );
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(resultSet.getInt("id"));
                ingredient.setName(resultSet.getString("name"));
                ingredient.setPrice(resultSet.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(resultSet.getString("category")));
                ingredient.setStockMovementList(
                        getStockMovementByIngredientId(connection, id)
                );
                return ingredient;
            }
            throw new RuntimeException("Ingredient not found " + id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public List<StockMovement> getStockMovementByIngredientId(Connection conn, int id) {
        String sql = """
            select id, quantity, type, unit, creation_datetime
            from stockmovement
            where id_ingredient = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            List<StockMovement> list = new ArrayList<>();
            while (rs.next()) {
                StockMovement m = new StockMovement();
                m.setId(rs.getInt("id"));

                StockValue sv = new StockValue();
                sv.setQuantity(rs.getDouble("quantity"));
                sv.setUnit(UnitType.valueOf(rs.getString("unit")));

                m.setValue(sv);
                m.setType(MovementTypeEnum.valueOf(rs.getString("type")));
                m.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());

                list.add(m);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =====================================================
    // DISH INGREDIENT
    // =====================================================
    private List<DishIngredient> findDishIngredientById(Integer idDish) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<DishIngredient> list = new ArrayList<>();

        String sql = """
            select d.id,
                   d.quantity_required,
                   d.unit,
                   i.id as ing_id,
                   i.name as ing_name,
                   i.price as ing_price,
                   i.category as ing_category
            from dish_ingredient d
            join ingredient i on d.id_ingredient = i.id
            where d.id_dish = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idDish);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DishIngredient di = new DishIngredient();
                Ingredient ing = new Ingredient();

                di.setId(rs.getInt("id"));
                di.setQuantity_required(rs.getDouble("quantity_required"));
                di.setUnit(UnitType.valueOf(rs.getString("unit")));

                ing.setId(rs.getInt("ing_id"));
                ing.setName(rs.getString("ing_name"));
                ing.setPrice(rs.getDouble("ing_price"));
                ing.setCategory(CategoryEnum.valueOf(rs.getString("ing_category")));

                // ✅ CORRECTION CLÉ
                ing.setStockMovementList(
                        getStockMovementByIngredientId(connection, ing.getId())
                );

                di.setIngredient(ing);
                list.add(di);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    // =====================================================
    // ORDER
    // =====================================================
    public Order saveOrder(Order orderToSave) {

        // 🔍 Vérification stock
        for (DishOrder dishOrder : orderToSave.getDishOrders()) {
            for (DishIngredient di : dishOrder.getDish().getDishIngredientList()) {

                double required = di.getQuantity_required() * dishOrder.getQuantity();
                Ingredient ing = di.getIngredient();

                if (ing.getStockMovementList().isEmpty()) {
                    ing.setStockMovementList(
                            findIngredientById(ing.getId()).getStockMovementList()
                    );
                }

                double available =
                        ing.getStockValueAt(Instant.now()).getQuantity();

                if (available < required) {
                    throw new RuntimeException(
                            "Insufficient stock for ingredient: " + ing.getName()
                    );
                }
            }
        }

        String sql = """
          insert into "Order"(id, reference, creation_datetime)
          values (?, ?, ?)
          on conflict (reference) do nothing
        """;


        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            int orderId = getNextSerialValue(connection, "Order", "id");
            orderToSave.setId(orderId);

            ps.setInt(1, orderId);
            ps.setString(2, orderToSave.getReference());
            ps.setTimestamp(3, Timestamp.from(orderToSave.getCreationDatetime()));

            ps.executeUpdate();

            saveDishOrder(connection, orderToSave.getDishOrders(), orderId);

            connection.commit();
            return orderToSave;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public void saveDishOrder(Connection conn, List<DishOrder> dishOrders, int orderId)
            throws SQLException {

        String sql = """
            insert into dish_order(id_order, id_dish, quantity)
            values (?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishOrder d : dishOrders) {
                ps.setInt(1, orderId);
                ps.setInt(2, d.getDish().getId());
                ps.setInt(3, d.getQuantity());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // =====================================================
    // SEQUENCE UTILS
    // =====================================================
    private int getNextSerialValue(Connection conn, String table, String column)
            throws SQLException {

        String seqSql = "SELECT pg_get_serial_sequence(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(seqSql)) {
            ps.setString(1, "\"" + table + "\"");
            ps.setString(2, column);
            ResultSet rs = ps.executeQuery();
            rs.next();
            String seq = rs.getString(1);

            try (PreparedStatement ps2 =
                         conn.prepareStatement("SELECT nextval(?)")) {
                ps2.setString(1, seq);
                ResultSet rs2 = ps2.executeQuery();
                rs2.next();
                return rs2.getInt(1);
            }
        }
    }
}
