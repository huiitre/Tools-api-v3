package fr.huiitre.tools.application.core.module.ports;

import java.util.List;
import java.util.Optional;

import fr.huiitre.tools.domain.core.module.Module;

public interface ModuleRepository {

    void save(Module module);

    void delete(Module module);

    void update(Module module);

    Optional<Module> findById(Long id);

    boolean existsByCode(String code);

    List<Module> findAll();
}
