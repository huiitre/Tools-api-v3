package fr.huiitre.tools.application.core.auth;

public interface EmailSender {

    void sendEmailVerification(
        String toEmail,
        String verificationLink
    );
}
