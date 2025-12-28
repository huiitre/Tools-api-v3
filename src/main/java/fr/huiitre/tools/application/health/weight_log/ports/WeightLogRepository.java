package fr.huiitre.tools.application.health.weight_log.ports;

import java.util.Optional;

import fr.huiitre.tools.domain.health.weight_log.WeightLog;

public interface WeightLogRepository {

    void save(Long userId, WeightLog weightLog);

    void update(Long userId, Long weightLogId, WeightLog weightLog);

    Optional<WeightLog> findById(Long userId, Long weightLogId);
}
