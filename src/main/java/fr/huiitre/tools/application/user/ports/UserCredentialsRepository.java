package fr.huiitre.tools.application.user.ports;

import java.util.Optional;

public interface UserCredentialsRepository {

    void save(Long userId, String passwordHash);

    Optional<String> findPasswordHashByUserId(Long userId);
}
