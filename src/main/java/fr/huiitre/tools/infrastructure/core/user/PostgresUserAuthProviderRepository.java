package fr.huiitre.tools.infrastructure.core.user;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;

import fr.huiitre.tools.application.core.auth.AuthProvider;
import fr.huiitre.tools.application.core.user.ports.UserAuthProviderRepository;

public class PostgresUserAuthProviderRepository implements UserAuthProviderRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostgresUserAuthProviderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean existsByUserIdAndProvider(Long userId, AuthProvider provider) {
        final String sql = """
            SELECT 1
            FROM tools_core.user_auth_provider
            WHERE user_id = ? AND provider = ?
            LIMIT 1
        """;

        return Boolean.TRUE.equals(
            jdbcTemplate.queryForObject(
                sql,
                Boolean.class,
                userId,
                provider.name()
            )
        );
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

        return Boolean.TRUE.equals(
            jdbcTemplate.queryForObject(
                sql,
                Boolean.class,
                provider.name(),
                providerUserId
            )
        );
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

        jdbcTemplate.update(
            sql,
            userId,
            provider.name(),
            providerUserId,
            providerEmail
        );
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

        return jdbcTemplate.query(
            sql,
            rs -> rs.next() ? Optional.of(rs.getLong("user_id")) : Optional.empty(),
            provider.name(),
            providerUserId
        );
    }
}
