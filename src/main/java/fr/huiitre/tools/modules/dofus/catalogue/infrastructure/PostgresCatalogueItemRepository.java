package fr.huiitre.tools.modules.dofus.catalogue.infrastructure;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import fr.huiitre.tools.modules.dofus.catalogue.api.dto.CatalogueSearchQuery;
import fr.huiitre.tools.modules.dofus.catalogue.application.dto.CatalogueItemDto;
import fr.huiitre.tools.modules.dofus.catalogue.application.ports.CatalogueItemRepository;

public class PostgresCatalogueItemRepository implements CatalogueItemRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final static Logger logger = LoggerFactory.getLogger(PostgresCatalogueItemRepository.class);

    public PostgresCatalogueItemRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<CatalogueItemDto> CATALOGUE_ITEM_DTO_ROW_MAPPER =
        (rs, rowNum) -> new CatalogueItemDto(
            rs.getLong("id"),
            rs.getLong("asset_id"),
            rs.getString("type"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getLong("level"),
            rs.getBoolean("has_recipe"),
            null
        );

    private static final RowMapper<CatalogueItemDto> CATALOGUE_INGREDIENT_ROW_MAPPER =
        (rs, rowNum) -> new CatalogueItemDto(
            rs.getLong("id"),
            rs.getLong("asset_id"),
            rs.getString("type"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getLong("level"),
            rs.getBoolean("has_recipe"),
            rs.getLong("quantity")
        );

    /**
     * Colonnes triables côté catalogue uniquement
     */
    private static final Map<String, String> SORT_COLUMNS = Map.of(
        "name", "i.name",
        "level", "i.level",
        "type", "it.name",
        "asset_id", "i.asset_id",
        "id", "i.id"
    );

    /**
     * QUERY CANONIQUE — SANS PRIX
     */
    private static final String BASE_QUERY = """
        SELECT
            i.id,
            i.asset_id,
            it.name AS type,
            i.name,
            i.description,
            i.level,

            EXISTS (
                SELECT 1
                FROM tools_dofus.recipe r
                WHERE r.item_id = i.id
            ) AS has_recipe

        FROM tools_dofus.item i
        LEFT JOIN tools_dofus.item_type it ON it.id = i.item_type_id

        WHERE (
            CAST(:qLike AS TEXT) IS NULL
            OR i.name ILIKE :qLike
            -- OR i.description ILIKE :qLike
        )
        """;

    @Override
    public List<CatalogueItemDto> search(
        CatalogueSearchQuery query,
        Long userId,        // conservé pour compatibilité interface
        Long gameServerId  // conservé pour compatibilité interface
    ) {
        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 20 : query.getPageSize();
        int offset = (page - 1) * pageSize;

        boolean hasSort =
            query.getSort() != null &&
            SORT_COLUMNS.containsKey(query.getSort());

        String orderBy = hasSort
            ? " ORDER BY " + SORT_COLUMNS.get(query.getSort()) + " " +
              (query.getDir() == CatalogueSearchQuery.Direction.DESC ? "DESC" : "ASC") +
              ", i.id ASC"
            : " ORDER BY i.id ASC";

        logger.info("Ligne #90 || orderBy : {}", orderBy);

        String sql = BASE_QUERY + orderBy + " LIMIT :limit OFFSET :offset";

        String qLike =
            query.getQ() == null || query.getQ().isBlank()
                ? null
                : "%" + query.getQ() + "%";

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("qLike", qLike)
            .addValue("limit", pageSize)
            .addValue("offset", offset);

        return jdbcTemplate.query(sql, params, CATALOGUE_ITEM_DTO_ROW_MAPPER);
    }

    @Override
    public Long count(
        CatalogueSearchQuery query,
        Long userId,        // conservé
        Long gameServerId  // conservé
    ) {
        String sql = """
            SELECT COUNT(*)
            FROM (
        """ + BASE_QUERY + """
            ) sub
        """;

        logger.info("Ligne #123 || sql : {}", sql);

        String qLike =
            query.getQ() == null || query.getQ().isBlank()
                ? null
                : "%" + query.getQ() + "%";

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("qLike", qLike);

        return jdbcTemplate.queryForObject(sql, params, Long.class);
    }

    @Override
    public List<CatalogueItemDto> findRecipeByItemId(Long itemId) {
        String sql = """
            SELECT
                i.id,
                i.asset_id,
                it.name AS type,
                i.name,
                i.description,
                i.level,

                EXISTS (
                    SELECT 1
                    FROM tools_dofus.recipe r
                    WHERE r.item_id = i.id
                ) AS has_recipe,

                r.quantity

            FROM tools_dofus.item i
            LEFT JOIN tools_dofus.item_type it ON it.id = i.item_type_id

            INNER JOIN tools_dofus.recipe r ON r.ingredient_id = i.id
            WHERE r.item_id = :itemId
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("itemId", itemId);

        return jdbcTemplate.query(sql, params, CATALOGUE_INGREDIENT_ROW_MAPPER);
    }
}
