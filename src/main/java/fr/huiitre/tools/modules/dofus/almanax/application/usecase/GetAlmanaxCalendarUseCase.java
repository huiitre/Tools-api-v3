package fr.huiitre.tools.modules.dofus.almanax.application.usecase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.modules.core.security.application.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.modules.core.security.application.usecase.SecuredUseCase;
import fr.huiitre.tools.modules.core.module.domain.ModuleCode;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.dofus.almanax.application.ports.AlmanaxRepository;
import fr.huiitre.tools.modules.dofus.almanax.application.view.AlmanaxDto;
import fr.huiitre.tools.modules.dofus.almanax.domain.Almanax;
import fr.huiitre.tools.modules.dofus.almanax.domain.DatePattern;
import fr.huiitre.tools.modules.dofus.game.application.ports.GameVersionRepository;
import fr.huiitre.tools.modules.dofus.game.application.view.GameVersionData;
import fr.huiitre.tools.modules.dofus.item.application.usecase.GetItemUseCase;
import fr.huiitre.tools.modules.dofus.item.application.view.ItemView;

@Service
@Transactional(readOnly = true)
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
            GameVersionRepository gameVersionRepository) {
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
                            pattern.score(date));
                }

                if (almanaxBestScoreForDay == 0) {
                    continue;
                }

                int patternCount = almanax.getDates().size();

                if (almanaxBestScoreForDay > bestScore ||
                        (almanaxBestScoreForDay == bestScore &&
                                patternCount < bestPatternCount)) {
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
                        bestAlmanax.getItemQuantity()));
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
                    missingDates);
        }

        return result;
    }
}