package fr.huiitre.tools.application.dofus.sync.almanax;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.dofus.gameversion.GameVersionData;
import fr.huiitre.tools.application.dofus.ports.providers.AlmanaxDataProvider;
import fr.huiitre.tools.application.dofus.ports.repositories.AlmanaxRepository;
import fr.huiitre.tools.application.dofus.sync.SyncReport;
import fr.huiitre.tools.domain.dofus.Almanax;
import fr.huiitre.tools.infrastructure.logging.DebugLogger;

@Service
@Transactional
public class SyncAlmanaxUseCase implements SecuredUseCase {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final AlmanaxDataProvider almanaxDataProvider;
    private final AlmanaxRepository almanaxRepository;

    private static final DebugLogger log = DebugLogger.of(SyncAlmanaxUseCase.class);
    private static final Logger logger = LoggerFactory.getLogger(SyncAlmanaxUseCase.class);

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.DOFUS);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    public SyncAlmanaxUseCase(
        AuthenticatedUserProvider authenticatedUserProvider,
        AlmanaxDataProvider almanaxDataProvider,
        AlmanaxRepository almanaxRepository
    ) {
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.almanaxDataProvider = almanaxDataProvider;
        this.almanaxRepository = almanaxRepository;
    }

    public SyncReport execute() {
        
        List<AlmanaxSyncData> external = almanaxDataProvider.fetchAll();

        Map<Long, Long> assetsIdCounts = external.stream()
            .collect(Collectors.groupingBy(AlmanaxSyncData::getAssetId, Collectors.counting()));

        assetsIdCounts.entrySet().stream()
            .filter(entry -> entry.getValue() > 1)
            .forEach(entry -> logger.warn("Asset ID {} is duplicated {} times", entry.getKey(), entry.getValue()));

        List<Almanax> current = almanaxRepository.findAll();

        Map<Long, Almanax> currentByAssetId = current.stream()
            .collect(Collectors.toMap(Almanax::getAssetId, item -> item));

        List<String> created = new java.util.ArrayList<>();
        List<String> updated = new java.util.ArrayList<>();

        logger.debug("Ligne #74 || updated : {}", updated);

        for (AlmanaxSyncData externalAlmanax : external) {

            Almanax existing = currentByAssetId.get(externalAlmanax.getAssetId());

            if (existing == null) {
                Almanax newAlmanax = Almanax.create(
                    externalAlmanax.getAssetId(),
                    externalAlmanax.getName(),
                    externalAlmanax.getDescription(),
                    externalAlmanax.getDates()
                );
                Long saved = almanaxRepository.save(newAlmanax);
                created.add("""
                        assetId=%d name=%s description=%s dates=%s id=%d
                        """.formatted(
                                externalAlmanax.getAssetId(),
                                externalAlmanax.getName(),
                                externalAlmanax.getDescription(),
                                externalAlmanax.getDates().toString(),
                                saved
                            )
                );
                continue;
            }

            boolean nameChanged = !existing.getName().equals(externalAlmanax.getName());
            boolean descriptionChanged = !existing.getDescription().equals(externalAlmanax.getDescription());
            boolean datesChanged = !existing.getDates().equals(externalAlmanax.getDates());

            if (nameChanged || descriptionChanged || datesChanged) {
                
                String oldName = existing.getName();
                String oldDescription = existing.getDescription();
                List<String> oldDates = existing.getDates();

                existing.update(
                    externalAlmanax.getName(),
                    externalAlmanax.getDescription(),
                    externalAlmanax.getDates()
                );

                almanaxRepository.update(existing);
                updated.add("""
                        id : %d
                        assetId : %d
                        name : %s -> %s
                        description : %s -> %s
                        dates : %s -> %s
                        """.formatted(
                                existing.getId(),
                                externalAlmanax.getAssetId(),
                                oldName,
                                externalAlmanax.getName(),
                                oldDescription,
                                externalAlmanax.getDescription(),
                                oldDates.toString(),
                                externalAlmanax.getDates().toString()
                            )
                );
            }
        }
        
        return new SyncReport(
            "items",
            "Items",
            created,
            updated);
    }
}