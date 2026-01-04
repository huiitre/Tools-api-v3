package fr.huiitre.tools.infrastructure.mail;

import java.nio.file.Path;
import java.util.List;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailSenderService {

    private final JavaMailSender mailSender;
    private final String from;
    private final List<String> to;

    public MailSenderService(
        JavaMailSender mailSender,
        @Value("${mail.from}") String from,
        @Value("${mail.to}") List<String> to
    ) {
        this.mailSender = mailSender;
        this.from = from;
        this.to = to;
    }

    public void send(String subject, String body) {
        sendInternal(subject, body, null);
    }

    public void sendWithAttachments(
        String subject,
        String body,
        List<Path> attachments
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(to.toArray(String[]::new));
            helper.setSubject(subject);
            helper.setText(body, false);

            for (Path attachment : attachments) {
                helper.addAttachment(
                    attachment.getFileName().toString(),
                    attachment.toFile()
                );
            }

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send mail with attachments", e);
        }
    }

    private void sendInternal(String subject, String body, Path attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                new MimeMessageHelper(message, attachment != null);

            helper.setFrom(from);
            helper.setTo(to.toArray(String[]::new));
            helper.setSubject(subject);
            helper.setText(body, false);

            if (attachment != null) {
                helper.addAttachment(
                    attachment.getFileName().toString(),
                    attachment.toFile()
                );
            }

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send mail", e);
        }
    }
}
