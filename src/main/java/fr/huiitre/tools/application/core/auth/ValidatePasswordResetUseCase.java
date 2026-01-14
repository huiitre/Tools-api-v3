package fr.huiitre.tools.application.core.auth;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import fr.huiitre.tools.application.core.auth.exception.InvalidPasswordResetTokenException;
import fr.huiitre.tools.application.core.user.ports.UserCredentialsRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ValidatePasswordResetUseCase {

    private final UserPasswordResetRepository userPasswordResetRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordHasher passwordHasher;

    public ValidatePasswordResetUseCase(
        UserPasswordResetRepository userPasswordResetRepository,
        UserCredentialsRepository userCredentialsRepository,
        PasswordHasher passwordHasher
    ) {
        this.userPasswordResetRepository = userPasswordResetRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.passwordHasher = passwordHasher;
    }

    public void execute(String token, String newPassword) {

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("PASSWORD_REQUIRED");
        }

        Long userId = userPasswordResetRepository
            .findUserIdByValidToken(token, LocalDateTime.now())
            .orElseThrow(() -> new InvalidPasswordResetTokenException("Invalid or expired password reset token"));

        String passwordHash = passwordHasher.hash(newPassword);

        userCredentialsRepository.updatePassword(
            userId,
            passwordHash
        );

        userPasswordResetRepository.deleteByUserId(userId);
    }
}
