package fr.huiitre.tools.application.core.module.ports;

import fr.huiitre.tools.domain.core.module.Module;

public interface ModuleRepository {

    void save(Module module);

    void delete(Module module);

    void update(Module module);
}
