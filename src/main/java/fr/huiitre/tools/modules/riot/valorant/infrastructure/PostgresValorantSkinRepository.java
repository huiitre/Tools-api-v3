package fr.huiitre.tools.modules.riot.valorant.infrastructure;

import fr.huiitre.tools.modules.riot.valorant.application.ports.ValorantSkinRepository;
import fr.huiitre.tools.modules.riot.valorant.application.view.ValorantSkinView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PostgresValorantSkinRepository implements ValorantSkinRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostgresValorantSkinRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<ValorantSkinView> SKIN_ROW_MAPPER = (rs, rowNum) -> new ValorantSkinView(
            rs.getLong("id"),
            rs.getObject("asset_id", UUID.class),
            rs.getString("name"),
            rs.getString("icon_url"),
            rs.getObject("tier_uuid", UUID.class),
            rs.getObject("content_tier_uuid", UUID.class));

    @Override
    public List<ValorantSkinView> findAll() {
        final String sql = """
                    SELECT id, asset_id, name, icon_url, tier_uuid, content_tier_uuid
                    FROM tools_riot.valorant_weapon_skins
                    ORDER BY name ASC
                """;
        return jdbcTemplate.query(sql, SKIN_ROW_MAPPER);
    }

    @Override
    public Optional<ValorantSkinView> findById(Long id) {
        final String sql = """
                    SELECT id, asset_id, name, icon_url, tier_uuid, content_tier_uuid
                    FROM tools_riot.valorant_weapon_skins
                    WHERE id = ?
                    LIMIT 1
                """;
        return jdbcTemplate.query(sql, SKIN_ROW_MAPPER, id).stream().findFirst();
    }
}
