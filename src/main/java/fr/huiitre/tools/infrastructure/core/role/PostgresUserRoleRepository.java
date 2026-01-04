package fr.huiitre.tools.infrastructure.core.role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.huiitre.tools.application.core.role.ports.UserRoleRepository;
import fr.huiitre.tools.domain.core.role.UserRole;
import fr.huiitre.tools.infrastructure.common.AbstractPostgresRepository;

public class PostgresUserRoleRepository extends AbstractPostgresRepository implements UserRoleRepository {

    private static final Logger logger = LoggerFactory.getLogger(PostgresUserRoleRepository.class);

    public PostgresUserRoleRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void save(UserRole userRole) {
        final String sql = """
                    INSERT INTO tools_core.user_role (user_id, role_id)
                    VALUES (?, ?)
                """;

        try {
            Connection conn = openConnection();
            logger.debug("TX CHECK PostgresUserRoleRepository auth_provider autocommit={}", conn.getAutoCommit());
            logger.debug(
                    "TX CHECK {} connHash={}",
                    this.getClass().getSimpleName(),
                    System.identityHashCode(conn));
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, userRole.getUserId());
                ps.setLong(2, userRole.getRoleId());

                ps.executeUpdate();

            }
        } catch (SQLException e) {
            throw sqlError("Failed to insert user role", e);
        }
    }
}
