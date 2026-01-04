package fr.huiitre.tools.infrastructure.dofus.persistence;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import fr.huiitre.tools.application.dofus.ports.repositories.ItemRepository;
import fr.huiitre.tools.domain.dofus.item.Item;
import fr.huiitre.tools.infrastructure.common.AbstractPostgresRepository;
import fr.huiitre.tools.infrastructure.filesystem.FileSystemChecker;

public class PostgresItemRepository extends AbstractPostgresRepository implements ItemRepository {

    @Value("${tools.assets.base-path}")
    private Path assetsBasePath;

    private final static Logger logger = LoggerFactory.getLogger(PostgresItemRepository.class);

    public PostgresItemRepository(DataSource dataSource) {
        super(dataSource);
    }

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
                FROM
                    tools_dofus.item
                WHERE
                    game_version_id = ?
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, gameVersionId);

                try (var rs = ps.executeQuery()) {
                    List<Item> items = new java.util.ArrayList<>();
                    while (rs.next()) {
                        Item item = Item.rehydrate(
                                rs.getLong("id"),
                                rs.getLong("asset_id"),
                                rs.getLong("game_version_id"),
                                rs.getLong("item_type_id"),
                                rs.getString("name"),
                                rs.getLong("level"),
                                rs.getString("description"));
                        items.add(item);
                    }
                    return items;
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to find item types by game version id", e);
        }
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
        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, item.getAssetId());
                ps.setLong(2, item.getGameVersionId());
                ps.setLong(3, item.getItemTypeId());
                ps.setString(4, item.getName());
                ps.setLong(5, item.getLevel());
                ps.setString(6, item.getDescription());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to retrieve generated module id");
                    }
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to save item : " + item, e);
        }
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
                    WHERE id = ? and game_version_id = ?
                """;
        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, item.getAssetId());
                ps.setLong(2, item.getGameVersionId());
                ps.setLong(3, item.getItemTypeId());
                ps.setString(4, item.getName());
                ps.setLong(5, item.getLevel());
                ps.setString(6, item.getDescription());
                ps.setLong(7, item.getId());
                ps.setLong(8, item.getGameVersionId());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to update item : " + item, e);
        }
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

        try {
            Connection conn = openConnection();

            // DELETE
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setLong(1, itemId);
                ps.executeUpdate();
            }

            // INSERT
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {

                if (images.has1x()) {
                    ps.setLong(1, itemId);
                    ps.setLong(2, iconId);
                    ps.setString(3, "X1");
                    ps.executeUpdate();
                }

                if (images.has2x()) {
                    ps.setLong(1, itemId);
                    ps.setLong(2, iconId);
                    ps.setString(3, "X2");
                    ps.executeUpdate();
                }
            }
            return true;

        } catch (SQLException e) {
            throw sqlError(
                "Failed to refresh item images for item id: " + itemId,
                e
            );
        }
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

    private record ImageExistence(boolean has1x, boolean has2x) {}
}
