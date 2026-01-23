package fr.huiitre.tools.modules.core.auth.application.usecase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.modules.core.auth.application.ports.EmailSender;

@Service
@Transactional
public class SendEmailVerificationUseCase {

    private final CreateEmailVerificationUseCase createEmailVerificationUseCase;
    private final EmailSender emailSender;
    private final String frontendBaseUrl;

    public SendEmailVerificationUseCase(
            CreateEmailVerificationUseCase createEmailVerificationUseCase,
            EmailSender emailSender,
            @Value("${app.frontend.base-url}") String frontendBaseUrl) {
        this.createEmailVerificationUseCase = createEmailVerificationUseCase;
        this.emailSender = emailSender;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public void execute(Long userId, String email) {

        // 1. Générer token + stocker
        String token = createEmailVerificationUseCase.execute(userId);

        // 2. Construire lien
        String link = frontendBaseUrl + "/auth/verify-email?token=" + token;

        // 3. Envoyer mail
        emailSender.sendEmailVerification(email, link);
    }
}
