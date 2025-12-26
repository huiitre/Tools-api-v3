package fr.huiitre.tools.infrastructure.persistence.core.user;

import fr.huiitre.tools.application.core.user.ports.UserRepository;
import fr.huiitre.tools.domain.core.user.User;
import fr.huiitre.tools.domain.core.user.UserType;
import fr.huiitre.tools.infrastructure.persistence.common.AbstractPostgresRepository;

import javax.sql.DataSource;

import java.sql.*;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresUserRepository extends AbstractPostgresRepository implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(PostgresUserRepository.class);

    public PostgresUserRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void save(User user) {
        final String sql = """
            INSERT INTO tools_core.users (name, email, is_active, user_type)
            VALUES (?, ?, ?, ?)
            RETURNING id
        """;

        try {
            Connection conn = openConnection();
            logger.debug("TX CHECK PostgresUserRepository auth_provider autocommit={}", conn.getAutoCommit());
            logger.debug(
                "TX CHECK {} connHash={}",
                this.getClass().getSimpleName(),
                System.identityHashCode(conn)
            );
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

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

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {

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

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {

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

            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by id", e);
        }
    }
}
