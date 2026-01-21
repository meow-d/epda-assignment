package com.crs.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EmailUtil {
    private static final String PROPERTIES_FILE = ".env";

    public static void sendEmail(String to, String subject, String body) throws MessagingException, IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(PROPERTIES_FILE)) {
            props.load(fis);
        }

        String host = props.getProperty("mail.smtp.host");
        String port = props.getProperty("mail.smtp.port");
        String username = props.getProperty("mail.smtp.username");
        String password = props.getProperty("mail.smtp.password");
        String auth = props.getProperty("mail.smtp.auth", "true");
        String starttls = props.getProperty("mail.smtp.starttls.enable", "true");

        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", host);
        mailProps.put("mail.smtp.port", port);
        mailProps.put("mail.smtp.auth", auth);
        mailProps.put("mail.smtp.starttls.enable", starttls);

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
