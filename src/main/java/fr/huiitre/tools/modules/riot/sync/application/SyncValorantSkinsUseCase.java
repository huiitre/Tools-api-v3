package fr.huiitre.tools.modules.riot.sync.application;

import fr.huiitre.tools.modules.core.module.domain.ModuleCode;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.core.security.application.usecase.SecuredUseCase;
import fr.huiitre.tools.modules.riot.valorant.application.view.ValorantSkinView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SyncValorantSkinsUseCase implements SecuredUseCase {

    private final ValorantSkinDataProvider skinDataProvider;
    private final ValorantSkinSyncRepository skinSyncRepository;

    public SyncValorantSkinsUseCase(
            ValorantSkinDataProvider skinDataProvider,
            ValorantSkinSyncRepository skinSyncRepository) {
        this.skinDataProvider = skinDataProvider;
        this.skinSyncRepository = skinSyncRepository;
    }

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.RIOT);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    public ValorantSyncReport execute() {
        List<ValorantSkinSyncData> external = skinDataProvider.fetchAll();

        Map<UUID, ValorantSkinView> currentByAssetId = skinSyncRepository.findAll().stream()
                .collect(Collectors.toMap(ValorantSkinView::assetId, it -> it));

        Set<UUID> externalAssetIds = external.stream()
                .map(ValorantSkinSyncData::getAssetId)
                .collect(Collectors.toSet());

        int created = 0;
        int updated = 0;
        int deleted = 0;

        for (ValorantSkinSyncData ext : external) {
            ValorantSkinView existing = currentByAssetId.get(ext.getAssetId());

            if (existing == null) {
                skinSyncRepository.save(ext);
                created++;
                continue;
            }

            boolean changed = !Objects.equals(existing.name(), ext.getName())
                    || !Objects.equals(existing.iconUrl(), ext.getIconUrl())
                    || !Objects.equals(existing.tierUuid(), ext.getTierUuid())
                    || !Objects.equals(existing.contentTierUuid(), ext.getContentTierUuid());

            if (changed) {
                skinSyncRepository.update(existing.id(), ext);
                updated++;
            }
        }

        for (ValorantSkinView current : currentByAssetId.values()) {
            if (!externalAssetIds.contains(current.assetId())) {
                skinSyncRepository.delete(current.id());
                deleted++;
            }
        }

        return new ValorantSyncReport(created, updated, deleted);
    }
}
