package fr.huiitre.tools.application.health.weight_log.ports;

import java.util.List;
import java.util.Optional;

import fr.huiitre.tools.application.health.weight_log.view.WeightLogView;
import fr.huiitre.tools.domain.health.weight_log.WeightLog;

public interface WeightLogRepository {

    void save(Long userId, WeightLog weightLog);

    void update(Long userId, Long weightLogId, WeightLog weightLog);

    Optional<WeightLog> findById(Long userId, Long weightLogId);

    void delete(Long userId, Long weightLogId);

    boolean existsById(Long userId, Long weightLogId);

    List<WeightLogView> findAllByUserId(Long userId);
}
