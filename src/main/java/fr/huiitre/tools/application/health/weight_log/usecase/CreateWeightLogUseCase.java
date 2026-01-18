package fr.huiitre.tools.application.health.weight_log.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.health.weight_log.command.CreateWeightLogCommand;
import fr.huiitre.tools.application.health.weight_log.ports.WeightLogRepository;
import fr.huiitre.tools.domain.health.weight_log.WeightLog;

@Service
@Transactional
public class CreateWeightLogUseCase implements SecuredUseCase {

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.HEALTH);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.USER;
    }

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final WeightLogRepository weightLogRepository;

    public CreateWeightLogUseCase(
            WeightLogRepository weightLogRepository,
            AuthenticatedUserProvider authenticatedUserProvider) {
        this.weightLogRepository = weightLogRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public void execute(
            CreateWeightLogCommand command) {

        Long userId = authenticatedUserProvider.getUserId();

        WeightLog weightLog = new WeightLog(
                command.getWeight(),
                command.getLogDate(),
                command.getNotes());

        weightLogRepository.save(userId, weightLog);
    }
}
