package fr.huiitre.tools.infrastructure.persistence.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import fr.huiitre.tools.application.user.ports.UserCredentialsRepository;

public class PostgresUserCredentialsRepository implements UserCredentialsRepository {

    private final DataSource dataSource;

    public PostgresUserCredentialsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Long userId, String passwordHash) {
        final String sql = """
            INSERT INTO tools_core.user_credentials (user_id, password_hash)
            VALUES (?, ?)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, passwordHash);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user credentials", e);
        }
    }

    @Override
    public Optional<String> findPasswordHashByUserId(Long userId) {

        final String sql = """
            SELECT password_hash
            FROM tools_core.user_credentials
            WHERE user_id = ?
            LIMIT 1
        """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(rs.getString("password_hash"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load password hash", e);
        }
    }
}
