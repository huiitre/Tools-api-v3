package fr.huiitre.tools.application.health.weight_log.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.health.weight_log.ports.WeightLogRepository;
import fr.huiitre.tools.application.health.weight_log.view.WeightLogView;

@Service
@Transactional
public class GetAllMyWeightLogUseCase implements SecuredUseCase {

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.TOOLS_HEALTH);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.USER;
    }

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final WeightLogRepository weightLogRepository;

    public GetAllMyWeightLogUseCase(
        WeightLogRepository weightLogRepository,
        AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.weightLogRepository = weightLogRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public List<WeightLogView> execute() {

        Long userId = authenticatedUserProvider.getUserId();

        return weightLogRepository.findAllByUserId(userId);
    }
}