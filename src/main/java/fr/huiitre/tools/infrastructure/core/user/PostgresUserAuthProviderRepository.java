package fr.huiitre.tools.infrastructure.core.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.huiitre.tools.application.core.auth.AuthProvider;
import fr.huiitre.tools.application.core.user.ports.UserAuthProviderRepository;
import fr.huiitre.tools.domain.core.user.User;
import fr.huiitre.tools.infrastructure.common.AbstractPostgresRepository;

public class PostgresUserAuthProviderRepository extends AbstractPostgresRepository
        implements UserAuthProviderRepository {

    private static final Logger logger = LoggerFactory.getLogger(PostgresUserAuthProviderRepository.class);

    public PostgresUserAuthProviderRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean existsByUserIdAndProvider(
            Long userId,
            AuthProvider provider) {
        final String sql = """
                    SELECT 1
                    FROM tools_core.user_auth_provider
                    WHERE user_id = ?
                    AND provider = ?
                    LIMIT 1
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {

                ps.setLong(1, userId);
                ps.setString(2, provider.name());

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check auth provider", e);
        }
    }

    @Override
    public boolean existsByProviderAndProviderUserId(
            AuthProvider provider,
            String providerUserId) {
        final String sql = """
                    SELECT 1
                    FROM tools_core.user_auth_provider
                    WHERE provider = ? AND provider_user_id = ?
                    LIMIT 1
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {

                ps.setString(1, provider.name());
                ps.setString(2, providerUserId);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check auth provider existence", e);
        }
    }

    @Override
    public void save(
            Long userId,
            AuthProvider provider,
            String providerUserId,
            String providerEmail) {
        final String sql = """
                    INSERT INTO tools_core.user_auth_provider
                        (user_id, provider, provider_user_id, provider_email)
                    VALUES (?, ?, ?, ?)
                """;

        try {
            Connection conn = openConnection();
            logger.debug("TX CHECK PostgresUserAuthProviderRepository auth_provider autocommit={}",
                    conn.getAutoCommit());
            logger.debug(
                    "TX CHECK {} connHash={}",
                    this.getClass().getSimpleName(),
                    System.identityHashCode(conn));
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {

                ps.setLong(1, userId);
                ps.setString(2, provider.name());
                ps.setString(3, providerUserId);
                ps.setString(4, providerEmail);

                ps.executeUpdate();

            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert auth provider", e);
        }
    }

    @Override
    public Optional<Long> findUserIdByProviderAndProviderUserId(
            AuthProvider provider,
            String providerUserId) {
        final String sql = """
                    SELECT user_id
                    FROM tools_core.user_auth_provider
                    WHERE provider = ? AND provider_user_id = ?
                    LIMIT 1
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {

                ps.setString(1, provider.name());
                ps.setString(2, providerUserId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Long userId = rs.getLong("user_id");
                        return Optional.of(userId);
                    } else {
                        return Optional.empty();
                    }
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user ID by auth provider", e);
        }
    }
}
