package org.td.service;

import org.td.config.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TableService {

    public List<Integer> findAvailableTables(Instant arrival, Instant departure) {

        String sql = """
            SELECT rt.table_number
            FROM restaurant_table rt
            WHERE rt.id NOT IN (
                SELECT to2.id_table
                FROM table_order to2
                WHERE NOT (
                    ? <= to2.arrival_datetime
                    OR ? >= to2.departure_datetime
                )
            )
        """;

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.from(departure));
            ps.setTimestamp(2, Timestamp.from(arrival));

            ResultSet rs = ps.executeQuery();
            List<Integer> tables = new ArrayList<>();

            while (rs.next()) {
                tables.add(rs.getInt("table_number"));
            }

            return tables;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
