package fr.huiitre.tools.application.core.user.ports;

import java.util.Optional;

public interface UserCredentialsRepository {

    void save(Long userId, String passwordHash);

    Optional<String> findPasswordHashByUserId(Long userId);

    void updatePassword(Long userId, String newPasswordHash);
}
