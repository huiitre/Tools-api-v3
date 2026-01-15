package fr.huiitre.tools.infrastructure.dofus.persistence;

import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fr.huiitre.tools.application.dofus.ports.repositories.ItemRepository;
import fr.huiitre.tools.domain.dofus.Item;
import fr.huiitre.tools.infrastructure.filesystem.FileSystemChecker;

public class PostgresItemRepository implements ItemRepository {

    @Value("${tools.assets.base-path}")
    private Path assetsBasePath;

    private static final Logger logger = LoggerFactory.getLogger(PostgresItemRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public PostgresItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Item> ITEM_ROW_MAPPER = (rs, rowNum) ->
        Item.rehydrate(
            rs.getLong("id"),
            rs.getLong("asset_id"),
            rs.getLong("game_version_id"),
            rs.getLong("item_type_id"),
            rs.getString("name"),
            rs.getLong("level"),
            rs.getString("description")
        );

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
            item.getDescription()
        );
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
            item.getGameVersionId()
        );
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
            "tools_dofus/dofus3/img/item/1x/" + iconId + "-64.png"
        );

        Path image2x = assetsBasePath.resolve(
            "tools_dofus/dofus3/img/item/2x/" + iconId + "-128.png"
        );

        return new ImageExistence(
            FileSystemChecker.exists(image1x),
            FileSystemChecker.exists(image2x)
        );
    }

    private record ImageExistence(boolean has1x, boolean has2x) {
    }
}
