package fr.huiitre.tools.infrastructure.persistence.core.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.huiitre.tools.application.core.user.ports.UserCredentialsRepository;
import fr.huiitre.tools.infrastructure.persistence.common.AbstractPostgresRepository;

public class PostgresUserCredentialsRepository extends AbstractPostgresRepository implements UserCredentialsRepository {

    private static final Logger logger = LoggerFactory.getLogger(PostgresUserCredentialsRepository.class);

    public PostgresUserCredentialsRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void save(Long userId, String passwordHash) {
        final String sql = """
            INSERT INTO tools_core.user_credentials (user_id, password_hash)
            VALUES (?, ?)
        """;

        try {
            Connection conn = openConnection();
            logger.debug("TX CHECK PostgresUserCredentialsRepository auth_provider autocommit={}", conn.getAutoCommit());
            logger.debug(
                "TX CHECK {} connHash={}",
                this.getClass().getSimpleName(),
                System.identityHashCode(conn)
            );
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {

                ps.setLong(1, userId);
                ps.setString(2, passwordHash);
                ps.executeUpdate();

            }
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

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {

                ps.setLong(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }

                    return Optional.of(rs.getString("password_hash"));
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load password hash", e);
        }
    }
}
