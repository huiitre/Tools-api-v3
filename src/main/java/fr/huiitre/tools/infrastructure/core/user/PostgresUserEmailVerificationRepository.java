package fr.huiitre.tools.infrastructure.core.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.huiitre.tools.application.core.auth.UserEmailVerificationRepository;
import fr.huiitre.tools.infrastructure.common.AbstractPostgresRepository;

public class PostgresUserEmailVerificationRepository extends AbstractPostgresRepository implements UserEmailVerificationRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(PostgresUserEmailVerificationRepository.class);

    public PostgresUserEmailVerificationRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void deleteByUserId(Long userId) {
        String sql = """
            DELETE FROM tools_core.user_email_verification
            WHERE user_id = ?
        """;

        try {
            Connection conn = openConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, userId);
                ps.executeUpdate();
            }
        } catch(SQLException e) {
            throw sqlError("Failed to delete email verification by user id", e);
        }
    }

    @Override
    public void save(Long userId, String token, java.time.LocalDateTime expiresAt) {
        String sql = """
            INSERT INTO tools_core.user_email_verification (user_id, token, expires_at)
            VALUES (?, ?, ?)
        """;

        try {
            Connection conn = openConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, userId);
                ps.setString(2, token);
                ps.setObject(3, expiresAt);
                ps.executeUpdate();
            }
        } catch(SQLException e) {
            throw sqlError("Failed to save email verification", e);
        }
    }

    @Override
    public Optional<Long> findUserIdByValidToken(String token, java.time.LocalDateTime now) {
        String sql = """
            SELECT user_id
            FROM tools_core.user_email_verification
            WHERE token = ?
            AND expires_at > ?
            LIMIT 1
        """;

        try {
            Connection conn = openConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, token);
                ps.setObject(2, now);

                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(rs.getLong("user_id"));
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch(SQLException e) {
            throw sqlError("Failed to find user id by valid token", e);
        }
    }

    @Override
    public void deleteExpired(LocalDateTime now) {
        String sql = """
            DELETE FROM tools_core.user_email_verification
            WHERE expires_at <= ?
        """;

        try {
            Connection conn = openConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, now);
                ps.executeUpdate();
            }
        } catch(SQLException e) {
            throw sqlError("Failed to delete expired email verifications", e);
        }
    }
}
