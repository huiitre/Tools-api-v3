package fr.huiitre.tools.infrastructure.core.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.sql.DataSource;

import fr.huiitre.tools.application.core.auth.UserPasswordResetRepository;
import fr.huiitre.tools.infrastructure.common.AbstractPostgresRepository;

public class PostgresUserPasswordResetRepository extends AbstractPostgresRepository implements UserPasswordResetRepository {
    
    public PostgresUserPasswordResetRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void save(Long userId, String token, LocalDateTime expiresAt) {
        String sql = """
            INSERT INTO tools_core.user_password_reset (user_id, token, expires_at)
            VALUES (?, ?, ?)
        """;

        try {
            Connection conn = openConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, userId);
                ps.setString(2, token);
                ps.setObject(3, expiresAt);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to save user password reset token", e);
        }
    }

    @Override
    public Optional<Long> findUserIdByValidToken(String token, LocalDateTime now) {
        String sql = """
            SELECT user_id
            FROM tools_core.user_password_reset
            WHERE token = ? AND expires_at > ?
        """;

        try {
            Connection conn = openConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setString(1, token);
                ps.setObject(2, now);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(rs.getLong("user_id"));
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to find user by valid password reset token", e);
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        String sql = """
            DELETE FROM tools_core.user_password_reset
            WHERE user_id = ?
        """;

        try {
            Connection conn = openConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, userId);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to delete user password reset tokens", e);
        }
    }

    @Override
    public void deleteExpired(LocalDateTime now) {
        String sql = """
            DELETE FROM tools_core.user_password_reset
            WHERE expires_at <= ?
        """;

        try {
            Connection conn = openConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setObject(1, now);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to delete expired user password reset tokens", e);
        }
    }
}
