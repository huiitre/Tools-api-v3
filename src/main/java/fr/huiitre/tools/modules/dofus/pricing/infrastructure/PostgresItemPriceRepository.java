package fr.huiitre.tools.modules.dofus.pricing.infrastructure;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import fr.huiitre.tools.modules.dofus.pricing.application.ports.ItemPriceRepository;
import fr.huiitre.tools.modules.dofus.pricing.application.view.ItemPriceDto;

public class PostgresItemPriceRepository implements ItemPriceRepository {

    private static final Logger logger = LoggerFactory.getLogger(PostgresItemPriceRepository.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostgresItemPriceRepository(
            NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<ItemPriceDto> ITEM_PRICE_DTO_ROW_MAPPER = (rs, rowNum) -> new ItemPriceDto(
            rs.getLong("item_id"),
            rs.getLong("user_price"),
            rs.getLong("community_average_price"),
            rs.getLong("last_updated_price"));

    @Override
    public List<ItemPriceDto> findPricesByItemIds(List<Long> itemIds, Long userId, Long serverId) {
        String sql = """
            SELECT
                ipu.item_id,

                /* Prix utilisateur */
                COALESCE(up.price, 0)::bigint AS user_price,

                /* Prix moyen communauté */
                COALESCE(cp.avg_price, 0)::bigint AS community_average_price,

                /* Dernier prix enregistré */
                COALESCE(lp.price, 0)::bigint AS last_updated_price

            FROM tools_dofus.item_price_user ipu

            /* Prix utilisateur */
            LEFT JOIN LATERAL (
                SELECT ipu2.price
                FROM tools_dofus.item_price_user ipu2
                WHERE ipu2.item_id = ipu.item_id
                AND ipu2.user_id = :userId
                AND ipu2.game_server_id = :serverId
                ORDER BY ipu2.created_at DESC
                LIMIT 1
            ) up ON TRUE

            /* Moyenne communauté */
            LEFT JOIN LATERAL (
                SELECT AVG(ipu3.price) AS avg_price
                FROM tools_dofus.item_price_user ipu3
                WHERE ipu3.item_id = ipu.item_id
                AND ipu3.game_server_id = :serverId
            ) cp ON TRUE

            /* Dernier prix global */
            LEFT JOIN LATERAL (
                SELECT ipu4.price
                FROM tools_dofus.item_price_user ipu4
                WHERE ipu4.item_id = ipu.item_id
                AND ipu4.game_server_id = :serverId
                ORDER BY ipu4.created_at DESC, ipu4.id DESC
                LIMIT 1
            ) lp ON TRUE

            WHERE ipu.item_id = ANY(:itemIds)
            GROUP BY ipu.item_id, up.price, cp.avg_price, lp.price
    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("itemIds", itemIds.toArray(new Long[0]))
            .addValue("userId", userId)
            .addValue("serverId", serverId);

        return jdbcTemplate.query(
            sql,
            params,
            ITEM_PRICE_DTO_ROW_MAPPER
        );
    }
}
