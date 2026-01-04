package fr.huiitre.tools.infrastructure.dofus.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import fr.huiitre.tools.application.dofus.gameversion.GameVersionData;
import fr.huiitre.tools.application.dofus.ports.repositories.GameVersionRepository;
import fr.huiitre.tools.infrastructure.common.AbstractPostgresRepository;

public class PostgresGameVersionRepository extends AbstractPostgresRepository implements GameVersionRepository {

    public PostgresGameVersionRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<GameVersionData> findById(Long gameVersionId) {
        final String sql = """
                    SELECT id, code, name
                    FROM tools_dofus.game_version
                    WHERE id = ?
                """;

        try {
            Connection conn = openConnection();
            try (
                    var ps = conn.prepareStatement(sql);) {
                ps.setLong(1, gameVersionId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(
                                new GameVersionData(
                                        rs.getLong("id"),
                                        rs.getString("name"),
                                        rs.getString("code")));
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to fetch game version by id: " + gameVersionId, e);
        }
    }
}
