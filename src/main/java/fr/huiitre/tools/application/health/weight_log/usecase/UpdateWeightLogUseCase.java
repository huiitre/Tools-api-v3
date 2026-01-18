package fr.huiitre.tools.application.health.weight_log.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.health.weight_log.command.UpdateWeightLogCommand;
import fr.huiitre.tools.application.health.weight_log.ports.WeightLogRepository;
import fr.huiitre.tools.domain.health.weight_log.WeightLog;

@Service
@Transactional
public class UpdateWeightLogUseCase implements SecuredUseCase {

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

    public UpdateWeightLogUseCase(
            WeightLogRepository weightLogRepository,
            AuthenticatedUserProvider authenticatedUserProvider) {
        this.weightLogRepository = weightLogRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public void execute(
            Long weightLogId,
            UpdateWeightLogCommand command) {

        Long userId = authenticatedUserProvider.getUserId();

        WeightLog weightLog = weightLogRepository.findById(userId, weightLogId)
                .orElseThrow(() -> new IllegalArgumentException("WEIGHT_LOG_NOT_FOUND"));

        weightLog.update(
                command.getWeight(),
                command.getNotes(),
                command.getLogDate());

        weightLogRepository.update(userId, weightLogId, weightLog);
    }
}
