package fr.huiitre.tools.infrastructure.persistence.core.role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import fr.huiitre.tools.application.core.role.ports.RoleRepository;
import fr.huiitre.tools.application.core.role.view.RoleView;
import fr.huiitre.tools.domain.core.role.Role;
import fr.huiitre.tools.infrastructure.persistence.common.AbstractPostgresRepository;

public class PostgresRoleRepository extends AbstractPostgresRepository implements RoleRepository {

    public PostgresRoleRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void save(Role role) {
        final String sql = """
            INSERT INTO tools_core.role (name, code, description, is_active)
            VALUES (?, ?, ?, ?)
            RETURNING id
        """;
        
        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {

                ps.setString(1, role.getName());
                ps.setString(2, role.getCode());
                ps.setString(3, role.getDescription());
                ps.setBoolean(4, role.getActive());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        role.setId(rs.getLong(1));
                    }
                    else {
                        throw new SQLException("Failed to retrieve generated role id");
                    }
                }

            }
        } catch (SQLException e) {
            throw sqlError("Failed to insert role", e);
        }
    }

    @Override
    public Optional<Role> findById(Long id) {
        final String sql = """
            SELECT id, code, name, description, is_active, created_at, updated_at
            FROM tools_core.role
            WHERE id = ?
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

                    Role role = new Role(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                    );

                    return Optional.of(role);
                }

            }
        } catch (SQLException e) {
            throw sqlError("Failed to find role by id", e);
        }
    }

    @Override
    public Optional<Role> findByCode(String code) {
        final String sql = """
            SELECT id, code, name, description, is_active, created_at, updated_at
            FROM tools_core.role
            WHERE code = ?
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                
                ps.setString(1, code);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next())
                        return Optional.empty();

                    Role role = new Role(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                    );

                    return Optional.of(role);
                }

            }
        } catch(SQLException e) {
            throw sqlError("Failed to find role by code", e);
        }
    }

    public List<RoleView> findAll() {
        final String sql = """
            SELECT id, code, name, description, is_active, created_at, updated_at
            FROM tools_core.role
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {

                try (ResultSet rs = ps.executeQuery()) {
                    List<RoleView> roleViews = new ArrayList<>();
                    while (rs.next()) {
                        RoleView roleView = new RoleView(
                            rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBoolean("is_active"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("updated_at").toLocalDateTime()
                        );
                        roleViews.add(roleView);
                    }
                    return roleViews;
                }

            }
        } catch (SQLException e) {
            throw sqlError("Failed to find all roles", e);
        }
    }
}
