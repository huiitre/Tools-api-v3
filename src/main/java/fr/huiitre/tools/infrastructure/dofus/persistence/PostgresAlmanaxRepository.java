package fr.huiitre.tools.infrastructure.dofus.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.huiitre.tools.application.dofus.ports.repositories.AlmanaxRepository;
import fr.huiitre.tools.domain.dofus.Almanax;
import fr.huiitre.tools.infrastructure.common.AbstractPostgresRepository;

public class PostgresAlmanaxRepository extends AbstractPostgresRepository implements AlmanaxRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public PostgresAlmanaxRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Almanax> findAll() {
        
        String sql = """
            SELECT
                id,
                asset_id,
                name,
                description,
                dates
            FROM
                tools_dofus.almanax
            """;
        
        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                try (var rs = ps.executeQuery()) {
                    List<Almanax> almanaxList = new ArrayList<>();
                    while (rs.next()) {
                        String jsonDates = rs.getString("dates");
                        List<String> dates = objectMapper.readValue(
                            jsonDates,
                            new TypeReference<List<String>>() {}
                        );
                        Almanax almanax = Almanax.rehydrate(
                                rs.getLong("id"),
                                rs.getLong("asset_id"),
                                rs.getString("name"),
                                rs.getString("description"),
                                dates
                        );
                        almanaxList.add(almanax);
                    }
                    return almanaxList;
                } catch (JsonProcessingException e) {
                    throw new SQLException("Failed to parse dates JSON", e);
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to find almanax by game version id", e);
        }
    }

    @Override
    public Long save(Almanax almanax) {
        String sql = """
            INSERT INTO tools_dofus.almanax (
                asset_id,
                name,
                description,
                dates
            ) VALUES (?, ?, ?, ?)
            RETURNING id
            """;
        
        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, almanax.getAssetId());
                ps.setString(2, almanax.getName());
                ps.setString(3, almanax.getDescription());
                String jsonDates = objectMapper.writeValueAsString(almanax.getDates());
                ps.setObject(4, objectMapper.writeValueAsString(almanax.getDates()), java.sql.Types.OTHER);

                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong("id");
                    }
                    throw new SQLException("Failed to retrieve generated ID for almanax");
                }
            } catch(JsonProcessingException e) {
                throw new SQLException("Failed to serialize dates to JSON", e);
            }
        } catch (SQLException e) {
            throw sqlError("Failed to save almanax", e);
        }
    }

    @Override
    public void update(Almanax almanax) {
        String sql = """
            UPDATE tools_dofus.almanax
            SET
                name = ?,
                description = ?,
                dates = ?
            WHERE
                id = ?
            """;
        
        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setString(1, almanax.getName());
                ps.setString(2, almanax.getDescription());
                String jsonDates = objectMapper.writeValueAsString(almanax.getDates());
                ps.setString(3, jsonDates);
                ps.setLong(4, almanax.getId());
                ps.executeUpdate();
            } catch(JsonProcessingException e) {
                throw new SQLException("Failed to serialize dates to JSON", e);
            }
        } catch (SQLException e) {
            throw sqlError("Failed to update almanax", e);
        }
    }
}
