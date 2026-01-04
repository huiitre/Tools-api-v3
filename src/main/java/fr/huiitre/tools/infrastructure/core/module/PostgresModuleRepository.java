package fr.huiitre.tools.infrastructure.core.module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.huiitre.tools.application.core.module.ports.ModuleRepository;
import fr.huiitre.tools.domain.core.module.Module;
import fr.huiitre.tools.infrastructure.common.AbstractPostgresRepository;

public class PostgresModuleRepository extends AbstractPostgresRepository implements ModuleRepository {

    private static final Logger logger = LoggerFactory.getLogger(PostgresModuleRepository.class);

    public PostgresModuleRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void save(Module module) {
        final String sql = """
                    INSERT INTO tools_core.module (name, description, code, is_active)
                    VALUES (?, ?, ?, ?)
                    RETURNING id
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setString(1, module.getName());
                ps.setString(2, module.getDescription());
                ps.setString(3, module.getCode());
                ps.setBoolean(4, module.getActive());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to retrieve generated module id");
                    }
                    module.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to insert module", e);
        }
    }

    @Override
    public void delete(Module module) {
        final String sql = """
                    DELETE FROM tools_core.module
                    WHERE id = ?
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, module.getId());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to delete module", e);
        }
    }

    @Override
    public void update(Module module) {
        final String sql = """
                    UPDATE tools_core.module
                    SET name = ?, description = ?, code = ?, is_active = ?
                    WHERE id = ?
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setString(1, module.getName());
                ps.setString(2, module.getDescription());
                ps.setString(3, module.getCode());
                ps.setBoolean(4, module.getActive());
                ps.setLong(5, module.getId());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to update module", e);
        }
    }

    @Override
    public Optional<Module> findById(Long id) {
        final String sql = """
                    SELECT id, name, description, code, is_active, created_at, updated_at
                    FROM tools_core.module
                    WHERE id = ?
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {

                ps.setLong(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }

                    Module module = new Module(
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("description"));

                    // champs techniques
                    module.setId(rs.getLong("id"));
                    module.setActive(rs.getBoolean("is_active"));
                    module.setCreatedAt(
                            rs.getTimestamp("created_at").toLocalDateTime());
                    module.setUpdatedAt(
                            rs.getTimestamp("updated_at") != null
                                    ? rs.getTimestamp("updated_at").toLocalDateTime()
                                    : null);
                    return Optional.of(module);
                }

            }
        } catch (SQLException e) {
            throw sqlError("Failed to find module by id", e);
        }
    }

    @Override
    public boolean existsByCode(String code) {
        final String sql = """
                    SELECT 1
                    FROM tools_core.module
                    WHERE code = ?
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {

                ps.setString(1, code);

                try (var rs = ps.executeQuery()) {
                    return rs.next();
                }

            }
        } catch (SQLException e) {
            throw sqlError("Failed to check existence of module by code", e);
        }
    }

    public List<Module> findAll() {
        final String sql = """
                    SELECT id, name, description, code, is_active, created_at, updated_at
                    FROM tools_core.module
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();) {
                List<Module> modules = new java.util.ArrayList<>();

                while (rs.next()) {
                    Module module = new Module(
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("description"));

                    // champs techniques
                    module.setId(rs.getLong("id"));
                    module.setActive(rs.getBoolean("is_active"));
                    module.setCreatedAt(
                            rs.getTimestamp("created_at").toLocalDateTime());
                    module.setUpdatedAt(
                            rs.getTimestamp("updated_at") != null
                                    ? rs.getTimestamp("updated_at").toLocalDateTime()
                                    : null);

                    modules.add(module);
                }

                return modules;
            }
        } catch (SQLException e) {
            throw sqlError("Failed to retrieve all modules", e);
        }
    }
}
