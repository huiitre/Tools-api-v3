package fr.huiitre.tools.application.core.auth;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserEmailVerificationRepository {
    
    void deleteByUserId(Long userId);

    void save(
        Long userId,
        String token,
        LocalDateTime expiresAt
    );

    Optional<Long> findUserIdByValidToken(String token, LocalDateTime now);

    void deleteExpired(LocalDateTime now);
}
