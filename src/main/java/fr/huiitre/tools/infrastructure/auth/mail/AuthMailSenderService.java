package fr.huiitre.tools.infrastructure.auth.mail;

import java.io.UnsupportedEncodingException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import fr.huiitre.tools.application.core.auth.EmailSender;

public class AuthMailSenderService implements EmailSender {

    private static final String FROM = "noreply@huiitre.fr";

    private final JavaMailSender mailSender;

    public AuthMailSenderService(
        JavaMailSender mailSender
    ) {
        this.mailSender = mailSender;
    }

    public void sendEmailVerification(
        String toEmail,
        String verificationLink
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(FROM, "Tools - Huiitre");
            helper.setTo(toEmail);
            helper.setSubject("Vérification de votre adresse email");
            helper.setText(
                """
                Bonjour,

                Merci de confirmer votre adresse email en cliquant sur le lien suivant :

                %s

                Ce lien expire dans 30 minutes.
                """.formatted(verificationLink),
                false
            );

            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("EMAIL_VERIFICATION_SEND_FAILED", e);
        }
    }
}
