package fr.huiitre.tools.infrastructure.dofus.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import fr.huiitre.tools.application.dofus.ports.repositories.ItemTypeRepository;
import fr.huiitre.tools.domain.dofus.ItemType;
import fr.huiitre.tools.infrastructure.common.AbstractPostgresRepository;

public class PostgresItemTypeRepository extends AbstractPostgresRepository implements ItemTypeRepository {

    public PostgresItemTypeRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<ItemType> findAllByGameVersionId(Long gameVersionId) {
        final String sql = """
                SELECT
                    id,
                    asset_id,
                    game_version_id,
                    category_id,
                    name
                FROM
                    tools_dofus.item_type
                WHERE
                    game_version_id = ?
                """;

        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, gameVersionId);

                try (var rs = ps.executeQuery()) {
                    List<ItemType> itemTypes = new java.util.ArrayList<>();
                    while (rs.next()) {
                        ItemType itemType = ItemType.rehydrate(
                                rs.getLong("id"),
                                rs.getLong("asset_id"),
                                rs.getLong("game_version_id"),
                                rs.getLong("category_id"),
                                rs.getString("name"));
                        itemTypes.add(itemType);
                    }
                    return itemTypes;
                }
            }
        } catch (SQLException e) {
            throw sqlError("Failed to find item types by game version id", e);
        }
    }

    @Override
    public void save(ItemType itemType) {
        final String sql = """
                    INSERT INTO tools_dofus.item_type (
                        asset_id,
                        game_version_id,
                        category_id,
                        name
                    ) VALUES (?, ?, ?, ?)
                """;
        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, itemType.getAssetId());
                ps.setLong(2, itemType.getGameVersionId());
                ps.setLong(3, itemType.getCategoryId());
                ps.setString(4, itemType.getName());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to save item type: " + itemType, e);
        }
    }

    @Override
    public void update(ItemType itemType) {
        final String sql = """
                    UPDATE tools_dofus.item_type
                    SET
                        asset_id = ?,
                        game_version_id = ?,
                        category_id = ?,
                        name = ?
                    WHERE id = ? and game_version_id = ?
                """;
        try {
            Connection conn = openConnection();
            try (
                    PreparedStatement ps = conn.prepareStatement(sql);) {
                ps.setLong(1, itemType.getAssetId());
                ps.setLong(2, itemType.getGameVersionId());
                ps.setLong(3, itemType.getCategoryId());
                ps.setString(4, itemType.getName());
                ps.setLong(5, itemType.getId());
                ps.setLong(6, itemType.getGameVersionId());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw sqlError("Failed to update item type: " + itemType, e);
        }
    }
}
