package fr.huiitre.tools.application.core.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SendPasswordResetUseCase {

    private final CreatePasswordResetUseCase createPasswordResetUseCase;
    private final EmailSender emailSender;
    private final String frontendBaseUrl;

    public SendPasswordResetUseCase(
        CreatePasswordResetUseCase createPasswordResetUseCase,
        EmailSender emailSender,
        @Value("${app.frontend.base-url}") String frontendBaseUrl
    ) {
        this.createPasswordResetUseCase = createPasswordResetUseCase;
        this.emailSender = emailSender;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public void execute(Long userId, String email) {

        String token = createPasswordResetUseCase.execute(userId);

        String link =
            frontendBaseUrl + "/auth/reset-password?token=" + token;

        emailSender.sendPasswordReset(email, link);
    }
}