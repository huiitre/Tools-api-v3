package fr.huiitre.tools.infrastructure.auth.scheduler;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.core.auth.UserEmailVerificationRepository;

@Component
public class EmailVerificationCleanupScheduler {

    private final UserEmailVerificationRepository userEmailVerificationRepository;

    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationCleanupScheduler.class);

    public EmailVerificationCleanupScheduler(
        UserEmailVerificationRepository userEmailVerificationRepository
    ) {
        this.userEmailVerificationRepository = userEmailVerificationRepository;
    }

    /**
     * Nettoie les tokens de validation email expirés.
     * Exécution toutes les 30 minutes.
     */
    @Scheduled(cron = "0 */30 * * * *")
    // @Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        logger.info("EMAIL_VERIFICATION_CLEANUP_START");
        userEmailVerificationRepository.deleteExpired(LocalDateTime.now());
        logger.info("EMAIL_VERIFICATION_CLEANUP_END");
    }
}
