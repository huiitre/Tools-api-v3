package fr.huiitre.tools.application.health.weight_log.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return Optional.of(ModuleCode.TOOLS_HEALTH);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.USER;
    }

    private final WeightLogRepository weightLogRepository;

    public CreateWeightLogUseCase(
        WeightLogRepository weightLogRepository
    ) {
        this.weightLogRepository = weightLogRepository;
    }

    public void execute(
        Long userId,
        CreateWeightLogCommand command
    ) {
        WeightLog weightLog = new WeightLog(
            command.getWeight(),
            command.getLogDate(),
            command.getNotes()
        );

        weightLogRepository.save(userId, weightLog);
    }
}
