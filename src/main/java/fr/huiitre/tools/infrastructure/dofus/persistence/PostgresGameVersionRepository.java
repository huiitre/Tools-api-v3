package fr.huiitre.tools.infrastructure.dofus.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;

import fr.huiitre.tools.application.dofus.gameversion.GameVersionData;
import fr.huiitre.tools.application.dofus.ports.repositories.GameVersionRepository;

public class PostgresGameVersionRepository implements GameVersionRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostgresGameVersionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<GameVersionData> findById(Long gameVersionId) {
        final String sql = """
            SELECT id, code, name
            FROM tools_dofus.game_version
            WHERE id = ?
        """;

        return jdbcTemplate
            .query(
                sql,
                (rs, rowNum) -> new GameVersionData(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("code")
                ),
                gameVersionId
            )
            .stream()
            .findFirst();
    }

    @Override
    public List<GameVersionData> findAll() {
        final String sql = """
            SELECT id, code, name
            FROM tools_dofus.game_version
            ORDER BY id ASC
        """;

        return jdbcTemplate
            .query(
                sql,
                (rs, rowNum) -> new GameVersionData(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("code")
                )
            );
    }
}
