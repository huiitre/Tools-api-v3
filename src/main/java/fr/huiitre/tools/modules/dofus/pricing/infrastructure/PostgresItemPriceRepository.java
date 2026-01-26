package fr.huiitre.tools.modules.dofus.pricing.infrastructure;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import fr.huiitre.tools.modules.dofus.pricing.application.ports.ItemPriceRepository;
import fr.huiitre.tools.modules.dofus.pricing.application.view.ItemPriceDto;

public class PostgresItemPriceRepository implements ItemPriceRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostgresItemPriceRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<ItemPriceDto> ITEM_PRICE_DTO_ROW_MAPPER =
        (rs, rowNum) -> new ItemPriceDto(
            rs.getLong("item_id"),

            rs.getLong("user_price"),
            rs.getLong("community_average_price"),
            rs.getLong("last_updated_price"),

            rs.getLong("craft_user_price"),
            rs.getLong("craft_community_price"),
            rs.getLong("craft_last_price"),
            rs.getLong("craft_calculated_price")
        );

    @Override
    public List<ItemPriceDto> findPricesByItemIds(
        List<Long> itemIds,
        Long userId,
        Long serverId
    ) {

        String sql = """
            SELECT
                i.id AS item_id,

                /* PRIX DIRECTS */
                COALESCE(up.price, 0)::bigint AS user_price,
                COALESCE(cp.avg_price, 0)::bigint AS community_average_price,
                COALESCE(lp.price, 0)::bigint AS last_updated_price,

                /* CRAFT — UNIQUEMENT SI RECETTE */
                CASE WHEN has_recipe THEN COALESCE(cu.price, 0)::bigint ELSE 0 END AS craft_user_price,
                CASE WHEN has_recipe THEN COALESCE(cc.price, 0)::bigint ELSE 0 END AS craft_community_price,
                CASE WHEN has_recipe THEN COALESCE(cl.price, 0)::bigint ELSE 0 END AS craft_last_price,
                CASE WHEN has_recipe THEN COALESCE(cbest.price, 0)::bigint ELSE 0 END AS craft_calculated_price

            FROM tools_dofus.item i

            /* Détection recette */
            LEFT JOIN LATERAL (
                SELECT EXISTS (
                    SELECT 1
                    FROM tools_dofus.recipe r
                    WHERE r.item_id = i.id
                ) AS has_recipe
            ) rflag ON TRUE

            /* PRIX UTILISATEUR */
            LEFT JOIN LATERAL (
                SELECT price
                FROM tools_dofus.item_price_user
                WHERE item_id = i.id
                  AND user_id = :userId
                  AND game_server_id = :serverId
                ORDER BY created_at DESC
                LIMIT 1
            ) up ON TRUE

            /* MOYENNE COMMU */
            LEFT JOIN LATERAL (
                SELECT AVG(price) AS avg_price
                FROM tools_dofus.item_price_user
                WHERE item_id = i.id
                  AND game_server_id = :serverId
            ) cp ON TRUE

            /* DERNIER PRIX GLOBAL */
            LEFT JOIN LATERAL (
                SELECT price
                FROM tools_dofus.item_price_user
                WHERE item_id = i.id
                  AND game_server_id = :serverId
                ORDER BY created_at DESC, id DESC
                LIMIT 1
            ) lp ON TRUE

            /* CRAFT — USER */
            LEFT JOIN LATERAL (
                SELECT SUM(r.quantity * COALESCE(p.price, 0)) AS price
                FROM tools_dofus.recipe r
                LEFT JOIN LATERAL (
                    SELECT price
                    FROM tools_dofus.item_price_user
                    WHERE item_id = r.ingredient_id
                      AND user_id = :userId
                      AND game_server_id = :serverId
                    ORDER BY created_at DESC
                    LIMIT 1
                ) p ON TRUE
                WHERE r.item_id = i.id
            ) cu ON TRUE

            /* CRAFT — COMMU */
            LEFT JOIN LATERAL (
                SELECT SUM(r.quantity * COALESCE(p.avg_price, 0)) AS price
                FROM tools_dofus.recipe r
                LEFT JOIN LATERAL (
                    SELECT AVG(price) AS avg_price
                    FROM tools_dofus.item_price_user
                    WHERE item_id = r.ingredient_id
                      AND game_server_id = :serverId
                ) p ON TRUE
                WHERE r.item_id = i.id
            ) cc ON TRUE

            /* CRAFT — DERNIER */
            LEFT JOIN LATERAL (
                SELECT SUM(r.quantity * COALESCE(p.price, 0)) AS price
                FROM tools_dofus.recipe r
                LEFT JOIN LATERAL (
                    SELECT price
                    FROM tools_dofus.item_price_user
                    WHERE item_id = r.ingredient_id
                      AND game_server_id = :serverId
                    ORDER BY created_at DESC, id DESC
                    LIMIT 1
                ) p ON TRUE
                WHERE r.item_id = i.id
            ) cl ON TRUE

            /* CRAFT — BEST (fallback simple = dernier) */
            LEFT JOIN LATERAL (
                SELECT SUM(r.quantity * COALESCE(p.price, 0)) AS price
                FROM tools_dofus.recipe r
                LEFT JOIN LATERAL (
                    SELECT price
                    FROM tools_dofus.item_price_user
                    WHERE item_id = r.ingredient_id
                      AND game_server_id = :serverId
                    ORDER BY created_at DESC, id DESC
                    LIMIT 1
                ) p ON TRUE
                WHERE r.item_id = i.id
            ) cbest ON TRUE

            WHERE i.id = ANY(:itemIds)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("itemIds", itemIds.toArray(new Long[0]))
            .addValue("userId", userId)
            .addValue("serverId", serverId);

        return jdbcTemplate.query(sql, params, ITEM_PRICE_DTO_ROW_MAPPER);
    }
}
