package fr.huiitre.tools.infrastructure.persistence.core.user_module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.huiitre.tools.application.core.user_module.ports.UserModuleRoleRepository;
import fr.huiitre.tools.infrastructure.persistence.common.AbstractPostgresRepository;
import fr.huiitre.tools.domain.core.user_module.UserModuleRole;

public class PostgresUserModuleRoleRepository extends AbstractPostgresRepository implements UserModuleRoleRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(PostgresUserModuleRoleRepository.class);

    public PostgresUserModuleRoleRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void save(UserModuleRole userModuleRole) {
        final String sql = """
            INSERT INTO tools_core.user_module_role (user_id, module_id, role_id)
            VALUES (?, ?, ?)
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {

                ps.setLong(1, userModuleRole.getUserId());
                ps.setLong(2, userModuleRole.getModuleId());
                ps.setLong(3, userModuleRole.getRoleId());

                ps.executeUpdate();

            }
        } catch (SQLException e) {
            throw sqlError("Failed to insert user module role", e);
        }
    }

    @Override
    public void deleteByUserIdAndModuleId(UserModuleRole userModuleRole) {
        final String sql = """
            DELETE FROM tools_core.user_module_role
            WHERE user_id = ? AND module_id = ?
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setLong(1, userModuleRole.getUserId());
                ps.setLong(2, userModuleRole.getModuleId());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to delete user module role", e);
        }
    }

    @Override
    public Optional<UserModuleRole> findByUserIdAndModuleId(Long userId, Long moduleId) {
        final String sql = """
            SELECT user_id, module_id, role_id
            FROM tools_core.user_module_role
            WHERE user_id = ? AND module_id = ?
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setLong(1, userId);
                ps.setLong(2, moduleId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }

                    UserModuleRole userModuleRole = new UserModuleRole(
                        rs.getLong("user_id"),
                        rs.getLong("module_id"),
                        rs.getLong("role_id")
                    );
                    return Optional.of(userModuleRole);
                }

            }
        } catch (SQLException e) {
            throw sqlError("Failed to find user module role by user id and module id", e);
        }
    }

    @Override
    public void deleteByModuleId(Long moduleId) {
        final String sql = """
            DELETE FROM tools_core.user_module_role
            WHERE module_id = ?
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setLong(1, moduleId);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to delete user module roles by module id", e);
        }
    }

    @Override
    public void updateRoleId(UserModuleRole userModuleRole) {
        final String sql = """
            UPDATE tools_core.user_module_role
            SET role_id = ?
            WHERE user_id = ? AND module_id = ?
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setLong(1, userModuleRole.getRoleId());
                ps.setLong(2, userModuleRole.getUserId());
                ps.setLong(3, userModuleRole.getModuleId());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to update user module role", e);
        }
    }
}
