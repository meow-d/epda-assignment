package com.crs.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    public static void sendEmail(String to, String subject, String body) throws MessagingException {
        String host = System.getenv("MAIL_SMTP_HOST");
        String port = System.getenv("MAIL_SMTP_PORT");
        String username = System.getenv("MAIL_SMTP_USERNAME");
        String password = System.getenv("MAIL_SMTP_PASSWORD");
        String auth = System.getenv("MAIL_SMTP_AUTH");
        String starttls = System.getenv("MAIL_SMTP_STARTTLS_ENABLE");

        if (host == null || port == null || username == null || password == null) {
            throw new MessagingException("Email environment variables not set");
        }

        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", host);
        mailProps.put("mail.smtp.port", port);
        mailProps.put("mail.smtp.auth", auth != null ? auth : "true");
        mailProps.put("mail.smtp.starttls.enable", starttls != null ? starttls : "true");

        Session session = Session.getInstance(mailProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }
}
