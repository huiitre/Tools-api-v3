package fr.huiitre.tools.infrastructure.todolist.todolist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import fr.huiitre.tools.application.todolist.todolist.ports.TodolistRepository;
import fr.huiitre.tools.domain.todolist.todolist.Todolist;
import fr.huiitre.tools.infrastructure.common.AbstractPostgresRepository;

public class PostgresTodolistRepository extends AbstractPostgresRepository implements TodolistRepository {

    public PostgresTodolistRepository(
            DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void save(Long userId, Todolist todolist) {
        final String sql = """
                    INSERT INTO tools_todolist.todolist (user_id, name, is_active, is_favorite, color_hex, display_order)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, userId);
                ps.setString(2, todolist.getName());
                ps.setBoolean(3, todolist.isActive());
                ps.setBoolean(4, todolist.isFavorite());
                ps.setString(5, todolist.getColorHex());
                ps.setLong(6, todolist.getDisplayOrder());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to insert todolist", e);
        }
    }

    @Override
    public void update(Long userId, Todolist todolist) {
        final String sql = """
                    UPDATE tools_todolist.todolist
                    SET name = ?, is_active = ?, is_favorite = ?, color_hex = ?, display_order = ?
                    WHERE user_id = ? AND id = ?
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setString(1, todolist.getName());
                ps.setBoolean(2, todolist.isActive());
                ps.setBoolean(3, todolist.isFavorite());
                ps.setString(4, todolist.getColorHex());
                ps.setLong(5, todolist.getDisplayOrder());
                ps.setLong(6, userId);
                ps.setLong(7, todolist.getId());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to update todolist", e);
        }
    }

    @Override
    public void delete(Long userId, Long todolistId) {
        final String sql = """
                    DELETE FROM tools_todolist.todolist
                    WHERE user_id = ? AND id = ?
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, userId);
                ps.setLong(2, todolistId);

                int affected = ps.executeUpdate();
                if (affected == 0) {
                    throw new IllegalArgumentException("TODOLIST_NOT_FOUND_OR_NOT_OWNED");
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to delete todolist", e);
        }
    }

    @Override
    public List<Todolist> findAllByUserId(Long userId) {
        final String sql = """
                    SELECT id, name, is_active, is_favorite, color_hex, display_order
                    FROM tools_todolist.todolist
                    WHERE user_id = ?
                    ORDER BY display_order ASC
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, userId);

                try (var rs = ps.executeQuery()) {
                    List<Todolist> todolists = new ArrayList<>();
                    while (rs.next()) {
                        Todolist todolist = Todolist.rehydrate(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getBoolean("is_active"),
                                rs.getBoolean("is_favorite"),
                                rs.getString("color_hex"),
                                rs.getLong("display_order"));
                        todolists.add(todolist);
                    }
                    return todolists;
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to find ²all todolists by user id", e);
        }
    }

    @Override
    public Optional<Todolist> findById(Long userId, Long todolistId) {
        final String sql = """
                    SELECT id, name, is_active, is_favorite, color_hex, display_order
                    FROM tools_todolist.todolist
                    WHERE user_id = ? AND id = ?
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, userId);
                ps.setLong(2, todolistId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    Todolist todolist = Todolist.rehydrate(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getBoolean("is_active"),
                            rs.getBoolean("is_favorite"),
                            rs.getString("color_hex"),
                            rs.getLong("display_order"));
                    return Optional.of(todolist);
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to find todolist by id", e);
        }
    }
}
