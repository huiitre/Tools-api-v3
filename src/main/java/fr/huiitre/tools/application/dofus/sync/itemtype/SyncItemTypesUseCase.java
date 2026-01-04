package fr.huiitre.tools.application.dofus.sync.itemtype;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.dofus.gameversion.GameVersionData;
import fr.huiitre.tools.application.dofus.ports.providers.ItemTypeDataProvider;
import fr.huiitre.tools.application.dofus.ports.repositories.ItemTypeRepository;
import fr.huiitre.tools.application.dofus.sync.SyncReport;
import fr.huiitre.tools.domain.dofus.itemtype.ItemType;

@Service
@Transactional
public class SyncItemTypesUseCase implements SecuredUseCase {

    private final ItemTypeRepository itemTypeRepository;
    private final ItemTypeDataProvider itemTypeDataProvider;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.TOOLS_DOFUS);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    public SyncItemTypesUseCase(
            ItemTypeRepository itemTypeRepository,
            ItemTypeDataProvider itemTypeDataProvider) {
        this.itemTypeRepository = itemTypeRepository;
        this.itemTypeDataProvider = itemTypeDataProvider;
    }

    public SyncReport execute(GameVersionData gameVersion) {

        List<ItemTypeSyncData> external =
                itemTypeDataProvider.fetchAll();

        List<ItemType> current =
                itemTypeRepository.findAllByGameVersionId(gameVersion.getId());

        Map<Long, ItemType> currentByAssetId =
                current.stream().collect(Collectors.toMap(
                        ItemType::getAssetId,
                        it -> it));

        List<String> created = new ArrayList<>();
        List<String> updated = new ArrayList<>();

        for (ItemTypeSyncData ext : external) {

            ItemType existing = currentByAssetId.get(ext.getAssetId());

            if (existing == null) {
                ItemType createdItem = ItemType.create(
                        ext.getAssetId(),
                        gameVersion.getId(),
                        ext.getCategoryId(),
                        ext.getName());

                itemTypeRepository.save(createdItem);

                created.add(
                        "assetId=" + ext.getAssetId()
                                + " name=\"" + ext.getName() + "\""
                                + " categoryId=" + ext.getCategoryId());
                continue;
            }

            boolean nameChanged = !existing.getName().equals(ext.getName());
            boolean categoryChanged = !existing.getCategoryId().equals(ext.getCategoryId());

            if (nameChanged || categoryChanged) {

                String oldName = existing.getName();
                Long oldCategoryId = existing.getCategoryId();

                existing.update(
                        ext.getName(),
                        ext.getCategoryId());

                itemTypeRepository.update(existing);

                updated.add(
                        "assetId=" + ext.getAssetId()
                                + (nameChanged
                                        ? " name=\"" + oldName + "\" -> \"" + ext.getName() + "\""
                                        : "")
                                + (categoryChanged
                                        ? " categoryId=" + oldCategoryId + " -> " + ext.getCategoryId()
                                        : ""));
            }
        }

        return new SyncReport(
            "item_types",
            "Item types",
            created,
            updated
        );
    }
}
