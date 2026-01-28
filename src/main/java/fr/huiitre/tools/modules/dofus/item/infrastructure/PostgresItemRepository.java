package fr.huiitre.tools.modules.dofus.item.infrastructure;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fr.huiitre.tools.modules.core.filesystem.infrastructure.FileSystemChecker;
import fr.huiitre.tools.modules.dofus.item.application.ports.ItemRepository;
import fr.huiitre.tools.modules.dofus.item.application.view.ItemImageDto;
import fr.huiitre.tools.modules.dofus.item.application.view.ItemView;
import fr.huiitre.tools.modules.dofus.item.domain.Item;
import fr.huiitre.tools.modules.dofus.itemtype.application.view.ItemTypeDto;

public class PostgresItemRepository implements ItemRepository {

    @Value("${tools.assets.base-path}")
    private Path assetsBasePath;

    private static final Logger logger = LoggerFactory.getLogger(PostgresItemRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public PostgresItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Item> ITEM_ROW_MAPPER = (rs, rowNum) -> Item.rehydrate(
            rs.getLong("id"),
            rs.getLong("asset_id"),
            rs.getLong("game_version_id"),
            rs.getLong("item_type_id"),
            rs.getString("name"),
            rs.getLong("level"),
            rs.getString("description"));

    @Override
    public List<Item> findAllByGameVersionId(Long gameVersionId) {
        final String sql = """
                    SELECT
                        id,
                        asset_id,
                        game_version_id,
                        item_type_id,
                        name,
                        level,
                        description
                    FROM tools_dofus.item
                    WHERE game_version_id = ?
                """;

        return jdbcTemplate.query(sql, ITEM_ROW_MAPPER, gameVersionId);
    }

    @Override
    public Long save(Item item) {
        final String sql = """
                    INSERT INTO tools_dofus.item (
                        asset_id,
                        game_version_id,
                        item_type_id,
                        name,
                        level,
                        description
                    ) VALUES (?, ?, ?, ?, ?, ?)
                    RETURNING id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                item.getAssetId(),
                item.getGameVersionId(),
                item.getItemTypeId(),
                item.getName(),
                item.getLevel(),
                item.getDescription());
    }

    @Override
    public void update(Item item) {
        final String sql = """
                    UPDATE tools_dofus.item
                    SET
                        asset_id = ?,
                        game_version_id = ?,
                        item_type_id = ?,
                        name = ?,
                        level = ?,
                        description = ?
                    WHERE id = ? AND game_version_id = ?
                """;

        jdbcTemplate.update(
                sql,
                item.getAssetId(),
                item.getGameVersionId(),
                item.getItemTypeId(),
                item.getName(),
                item.getLevel(),
                item.getDescription(),
                item.getId(),
                item.getGameVersionId());
    }

    @Override
    public boolean refreshImages(Long itemId, Long iconId) {
        ImageExistence images = checkItemImagesExistence(iconId);

        final String deleteSql = """
                    DELETE FROM tools_dofus.item_image
                    WHERE item_id = ?
                """;

        final String insertSql = """
                    INSERT INTO tools_dofus.item_image (
                        item_id,
                        icon_id,
                        resolution
                    ) VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(deleteSql, itemId);

        if (images.has1x()) {
            jdbcTemplate.update(insertSql, itemId, iconId, "X1");
        }

        if (images.has2x()) {
            jdbcTemplate.update(insertSql, itemId, iconId, "X2");
        }

        return true;
    }

    private ImageExistence checkItemImagesExistence(Long iconId) {

        Path image1x = assetsBasePath.resolve(
                "tools_dofus/dofus3/img/item/1x/" + iconId + "-64.png");

        Path image2x = assetsBasePath.resolve(
                "tools_dofus/dofus3/img/item/2x/" + iconId + "-128.png");

        return new ImageExistence(
                FileSystemChecker.exists(image1x),
                FileSystemChecker.exists(image2x));
    }

    @Override
    public Optional<Item> findByAssetId(Long assetId, long gameVersionId) {
        final String sql = """
                    SELECT
                        id,
                        asset_id,
                        game_version_id,
                        item_type_id,
                        name,
                        level,
                        description
                    FROM tools_dofus.item
                    WHERE asset_id = ? AND game_version_id = ?
                """;

        List<Item> items = jdbcTemplate.query(sql, ITEM_ROW_MAPPER, assetId, gameVersionId);

        if (items.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(items.get(0));
        }
    }

    private static final RowMapper<ItemView> ITEM_VIEW_ROW_MAPPER = (rs, rowNum) -> {

        ItemTypeDto itemTypeDto = new ItemTypeDto(
                rs.getLong("item_type_id"),
                rs.getLong("item_type_asset_id"),
                rs.getLong("item_type_game_version_id"),
                rs.getString("item_type_name"));

        return new ItemView(
                rs.getLong("id"),
                rs.getLong("asset_id"),
                rs.getLong("game_version_id"),
                rs.getString("name"),
                rs.getLong("level"),
                rs.getString("description"),
                itemTypeDto,
                List.of(),
                rs.getBoolean("has_recipe"));
    };

    @Override
    public ItemView findById(Long itemId, Long gameVersionId, Long userId) {
        final String sql = """
                    SELECT
                        i.id,
                        i.asset_id,
                        i.game_version_id,
                        i.name,
                        i.level,
                        i.description,
                        it.id AS item_type_id,
                        it.asset_id AS item_type_asset_id,
                        it.name AS item_type_name,
                        it.game_version_id AS item_type_game_version_id,
                        COALESCE(MAX(user_price.price), 0)::bigint AS user_price,
                        COALESCE(ROUND(AVG(community_price.price)), 0)::bigint AS community_price,
                        COALESCE(MAX(last_price.price), 0)::bigint AS last_updated_price,
                        EXISTS (
                            SELECT 1
                            FROM tools_dofus.recipe r
                            WHERE r.item_id = i.id
                        ) AS has_recipe
                    FROM tools_dofus.item i
                    JOIN tools_dofus.item_type it
                        ON i.item_type_id = it.id
                    AND i.game_version_id = it.game_version_id

                    /* Prix personnalisé de l'utilisateur */
                    LEFT JOIN tools_dofus.item_price_user user_price
                        ON i.id = user_price.item_id
                    AND user_price.user_id = ?
                    AND user_price.game_server_id = 1

                    /* Prix moyen de tous les joueurs */
                    LEFT JOIN tools_dofus.item_price_user community_price
                        ON i.id = community_price.item_id
                    AND community_price.game_server_id = 1

                    /* Dernier prix ajouté pour l'objet (tous utilisateurs confondus) */
                    LEFT JOIN LATERAL (
                        SELECT ipu.price
                        FROM tools_dofus.item_price_user ipu
                        WHERE ipu.item_id = i.id
                        AND ipu.game_server_id = 1
                        ORDER BY
                            ipu.created_at DESC,
                            (ipu.user_id = ?) DESC,
                            ipu.id DESC
                        LIMIT 1
                    ) last_price ON TRUE

                    WHERE i.id = ?
                    AND i.game_version_id = ?

                    GROUP BY
                        i.id,
                        i.asset_id,
                        i.game_version_id,
                        i.name,
                        i.level,
                        i.description,
                        it.id,
                        it.asset_id,
                        it.name,
                        it.game_version_id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                ITEM_VIEW_ROW_MAPPER,
                userId,
                userId,
                itemId,
                gameVersionId);
    }

    private static final RowMapper<ItemImageDto> ITEM_IMAGE_DTO_ROW_MAPPER = (rs, rowNum) -> {

        return new ItemImageDto(
                rs.getLong("id"),
                rs.getLong("item_id"),
                rs.getString("resolution"),
                rs.getLong("icon_id"));
    };

    @Override
    public List<ItemImageDto> findImageByItemId(Long itemId) {
        final String sql = """
                    SELECT
                        id,
                        item_id,
                        icon_id,
                        resolution
                    FROM tools_dofus.item_image
                    WHERE item_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                ITEM_IMAGE_DTO_ROW_MAPPER,
                itemId);
    }

    @Override
    public Map<Long, ItemView> findByGameVersionIdAndItemIds(Long gameVersionId, Set<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return Map.of();
        }

        final String sql = """
                SELECT
                    i.id,
                    i.asset_id,
                    i.game_version_id,
                    i.name,
                    i.level,
                    i.description,
                    it.id AS item_type_id,
                    it.asset_id AS item_type_asset_id,
                    it.name AS item_type_name,
                    it.game_version_id AS item_type_game_version_id,
                    EXISTS (
                        SELECT 1
                        FROM tools_dofus.recipe r
                        WHERE r.item_id = i.id
                    ) AS has_recipe
                FROM tools_dofus.item i
                JOIN tools_dofus.item_type it
                    ON i.item_type_id = it.id
                    AND i.game_version_id = it.game_version_id
                WHERE i.game_version_id = ?
                AND i.id = ANY(?)
                """;

        Long[] itemIdsArray = itemIds.toArray(new Long[0]);

        List<ItemView> items = jdbcTemplate.query(
                sql,
                ITEM_VIEW_ROW_MAPPER,
                gameVersionId,
                itemIdsArray);

        return items.stream()
                .collect(Collectors.toMap(ItemView::getId, Function.identity()));
    }

    private record ImageExistence(boolean has1x, boolean has2x) {
    }
}
