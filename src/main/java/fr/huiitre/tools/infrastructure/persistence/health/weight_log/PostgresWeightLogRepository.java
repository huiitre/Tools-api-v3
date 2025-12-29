package fr.huiitre.tools.infrastructure.persistence.health.weight_log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.huiitre.tools.application.health.weight_log.ports.WeightLogRepository;
import fr.huiitre.tools.application.health.weight_log.view.WeightLogView;
import fr.huiitre.tools.domain.health.weight_log.WeightLog;
import fr.huiitre.tools.infrastructure.persistence.common.AbstractPostgresRepository;

public class PostgresWeightLogRepository extends AbstractPostgresRepository implements WeightLogRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(PostgresWeightLogRepository.class);

    public PostgresWeightLogRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void save(Long userId, WeightLog weightLog) {
        final String sql = """
            INSERT INTO tools_health.weight_log (user_id, weight, logged_at, notes)
            VALUES (?, ?, ?, ?)
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setLong(1, userId);
                ps.setDouble(2, weightLog.getWeight());
                ps.setTimestamp(3, java.sql.Timestamp.valueOf(weightLog.getLogDate()));
                ps.setString(4, weightLog.getNotes());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to insert weight log", e);
        }
    }

    @Override
    public void update(Long userId, Long weightLogId, WeightLog weightLog) {
        final String sql = """
            UPDATE tools_health.weight_log
            SET weight = ?, logged_at = ?, notes = ?
            WHERE user_id = ? AND id = ?
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setDouble(1, weightLog.getWeight());
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(weightLog.getLogDate()));
                ps.setString(3, weightLog.getNotes());
                ps.setLong(4, userId);
                ps.setLong(5, weightLogId);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to update weight log", e);
        }
    }

    @Override
    public Optional<WeightLog> findById(Long userId, Long weightLogId) {
        final String sql = """
            SELECT weight, logged_at, notes
            FROM tools_health.weight_log
            WHERE user_id = ? AND id = ?
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setLong(1, userId);
                ps.setLong(2, weightLogId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        WeightLog weightLog = new WeightLog(
                            rs.getDouble("weight"),
                            rs.getTimestamp("logged_at").toLocalDateTime(),
                            rs.getString("notes")
                        );
                        return Optional.of(weightLog);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to find weight log by id", e);
        }
    }

    @Override
    public void delete(Long userId, Long weightLogId) {
        final String sql = """
            DELETE FROM tools_health.weight_log
            WHERE user_id = ? AND id = ?
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setLong(1, userId);
                ps.setLong(2, weightLogId);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to delete weight log", e);
        }
    }

    @Override
    public boolean existsById(Long userId, Long weightLogId) {
        final String sql = """
            SELECT 1
            FROM tools_health.weight_log
            WHERE user_id = ? AND id = ?
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setLong(1, userId);
                ps.setLong(2, weightLogId);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to check existence of weight log by id", e);
        }
    }

    @Override
    public List<WeightLogView> findAllByUserId(Long userId) {
        final String sql = """
            SELECT id, weight, logged_at, notes
            FROM tools_health.weight_log
            WHERE user_id = ?
            ORDER BY logged_at DESC
        """;

        try {
            Connection conn = openConnection();
            try (
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setLong(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    List<WeightLogView> weightLogs = new java.util.ArrayList<>();
                    while (rs.next()) {
                        WeightLogView weightLog = new WeightLogView(
                            rs.getLong("id"),
                            rs.getDouble("weight"),
                            rs.getTimestamp("logged_at").toLocalDateTime(),
                            rs.getString("notes")
                        );
                        weightLogs.add(weightLog);
                    }
                    return weightLogs;
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to find all weight logs by user id", e);
        }
    }
}
