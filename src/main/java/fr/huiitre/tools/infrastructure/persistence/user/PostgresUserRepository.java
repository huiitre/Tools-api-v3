package fr.huiitre.tools.infrastructure.persistence.user;

import fr.huiitre.tools.application.user.ports.UserRepository;
import fr.huiitre.tools.domain.user.User;
import fr.huiitre.tools.domain.user.UserType;

import javax.sql.DataSource;

import java.sql.*;
import java.util.Optional;

public class PostgresUserRepository implements UserRepository {

    private final DataSource dataSource;

    public PostgresUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(User user) {
        final String sql = """
            INSERT INTO tools_core.users (name, email, is_active, user_type)
            VALUES (?, ?, ?, ?)
            RETURNING id
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setBoolean(3, false);
            ps.setString(4, user.getUserType().name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Failed to retrieve generated user id");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {

        final String sql = """
            SELECT id, name, email, is_active, user_type
            FROM tools_core.users
            WHERE email = ?
            LIMIT 1
        """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                User user = new User(
                    rs.getString("name"),
                    rs.getString("email"),
                    UserType.valueOf(rs.getString("user_type"))
                );

                user.setId(rs.getLong("id"));
                user.setIsActive(rs.getBoolean("is_active"));

                return Optional.of(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by email", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {

        final String sql = """
            SELECT id, name, email, is_active, user_type
            FROM tools_core.users
            WHERE id = ?
            LIMIT 1
        """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                User user = new User(
                    rs.getString("name"),
                    rs.getString("email"),
                    UserType.valueOf(rs.getString("user_type"))
                );

                user.setId(rs.getLong("id"));
                user.setIsActive(rs.getBoolean("is_active"));

                return Optional.of(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by id", e);
        }
    }
}
