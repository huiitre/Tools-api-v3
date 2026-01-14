package fr.huiitre.tools.application.core.user.ports;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.cglib.core.Local;

import fr.huiitre.tools.domain.core.user.User;

public interface UserRepository {

    void save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    void deleteUnvalidatedUsersWithExpiredEmailVerification(LocalDateTime now);

    void deleteUnvalidatedUsersWithoutEmailVerification();
}
