package fr.huiitre.tools.application.core.role.ports;

import java.util.Optional;

import fr.huiitre.tools.domain.core.role.Role;

public interface RoleRepository {

    void save(Role role);

    Optional<Role> findById(Long id);

    Optional<Role> findByCode(String code);
}
