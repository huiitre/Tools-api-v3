package fr.huiitre.tools.application.core.user.ports;

import java.util.Optional;

import fr.huiitre.tools.domain.core.user.User;

public interface UserRepository {

    void save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);
}
