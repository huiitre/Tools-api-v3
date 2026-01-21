package fr.huiitre.tools.application.dofus.almanax;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.api.dofus.AlmanaxDto;
import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.dofus.game.GameVersionData;
import fr.huiitre.tools.application.dofus.item.GetItemUseCase;
import fr.huiitre.tools.application.dofus.item.ItemView;
import fr.huiitre.tools.application.dofus.ports.repositories.AlmanaxRepository;
import fr.huiitre.tools.domain.dofus.Almanax;
import fr.huiitre.tools.domain.dofus.DatePattern;
import fr.huiitre.tools.application.dofus.ports.repositories.GameVersionRepository;

@Service
@Transactional
public class GetAlmanaxCalendarUseCase implements SecuredUseCase {

    private final AlmanaxRepository almanaxRepository;

    private final GameVersionRepository gameVersionRepository;

    private final GetItemUseCase getItemUseCase;

    private static final Logger logger = LoggerFactory.getLogger(GetAlmanaxCalendarUseCase.class);

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.DOFUS);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.READ_ONLY;
    }

    public GetAlmanaxCalendarUseCase(
        AlmanaxRepository almanaxRepository,
        AuthenticatedUserProvider authenticatedUserProvider,
        GetItemUseCase getItemUseCase,
        GameVersionRepository gameVersionRepository
    ) {
        this.almanaxRepository = almanaxRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.getItemUseCase = getItemUseCase;
        this.gameVersionRepository = gameVersionRepository;
    }

    public List<AlmanaxDto> execute() {

        List<Almanax> almanaxList = almanaxRepository.findAll();

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusYears(2);

        List<AlmanaxDto> result = new ArrayList<>();

        List<LocalDate> missingDates = new ArrayList<>();

        GameVersionData gameVersion = gameVersionRepository.findByCode("dofus3");

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

            Almanax bestAlmanax = null;
            int bestScore = 0;
            int bestPatternCount = Integer.MAX_VALUE;

            for (Almanax almanax : almanaxList) {

                int almanaxBestScoreForDay = 0;

                for (String rawPattern : almanax.getDates()) {
                    DatePattern pattern = new DatePattern(rawPattern);
                    almanaxBestScoreForDay = Math.max(
                        almanaxBestScoreForDay,
                        pattern.score(date)
                    );
                }

                if (almanaxBestScoreForDay == 0) {
                    continue;
                }

                int patternCount = almanax.getDates().size();

                if (
                    almanaxBestScoreForDay > bestScore ||
                    (
                        almanaxBestScoreForDay == bestScore &&
                        patternCount < bestPatternCount
                    )
                ) {
                    bestScore = almanaxBestScoreForDay;
                    bestPatternCount = patternCount;
                    bestAlmanax = almanax;
                }
            }

            if (bestAlmanax != null) {

                ItemView itemView = getItemUseCase.execute(gameVersion.getId(), bestAlmanax.getItemId())
                    .orElse(null);

                result.add(new AlmanaxDto(
                    bestAlmanax.getId(),
                    bestAlmanax.getName(),
                    bestAlmanax.getDescription(),
                    date,
                    itemView,
                    bestAlmanax.getItemQuantity()
                ));
            } else {
                missingDates.add(date);
            }
        }

        if (!missingDates.isEmpty()) {
            logger.debug(
                "Almanax missing for {} days between {} and {}: {}",
                missingDates.size(),
                start,
                end,
                missingDates
            );
        }

        return result;
    }
}