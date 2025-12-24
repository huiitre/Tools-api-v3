package fr.huiitre.tools.infrastructure.persistence.user;

import fr.huiitre.tools.application.user.ports.UserAuthProviderRepository;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import fr.huiitre.tools.application.auth.Authprovider;

public class PostgresUserAuthProviderRepository implements UserAuthProviderRepository {

    private final DataSource dataSource;

    public PostgresUserAuthProviderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean existsByUserIdAndProvider(
        Long userId,
        Authprovider provider
    ) {
        final String sql = """
            SELECT 1
            FROM tools_core.user_auth_provider
            WHERE user_id = ?
            AND provider = ?
            LIMIT 1
        """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, provider.name());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check auth provider", e);
        }
    }

    @Override
    public boolean existsByProviderAndProviderUserId(
        Authprovider provider,
        String providerUserId
    ) {
        final String sql = """
            SELECT 1
            FROM tools_core.user_auth_provider
            WHERE provider = ? AND provider_user_id = ?
            LIMIT 1
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, provider.name());
            ps.setString(2, providerUserId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check auth provider existence", e);
        }
    }

    @Override
    public void save(
        Long userId,
        Authprovider provider,
        String providerUserId,
        String providerEmail
    ) {
        final String sql = """
            INSERT INTO tools_core.user_auth_provider
                (user_id, provider, provider_user_id, provider_email)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, provider.name());
            ps.setString(3, providerUserId);
            ps.setString(4, providerEmail);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert auth provider", e);
        }
    }
}
