package com.example.semestralkaa.services;

import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.example.semestralkaa.entity.ResetToken;
import com.example.semestralkaa.entity.User;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private UserService userService;

    Properties properties = System.getProperties();

    //Api key: 4e17ac5eb87c7569cbfd878a63e437f3-3724298e-9c98952c

    public EmailService() {
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        sf.setTrustAllHosts(true);

        // Setup mail server
        properties.put("mail.smtp.host", "smtp.mailgun.org");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);
    }

    public void sendResetTokenEmail(User user) throws MessagingException {
        if(user == null) throw new RuntimeException("User is null");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("postmaster@sandbox52d5c37deba9467a95f5ad2766a93700.mailgun.org", "47c4b380b0b6a3318fc39b46acd50613-3724298e-812419a8");

            }

        });

        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            UUID uuid = UUID.randomUUID();
            String token = uuid.toString();

            userService.deactivateUserResetTokens(user);

            ResetToken resetToken = new ResetToken(user, token);
            user.getResetTokens().add(resetToken);

            message.setFrom(new InternetAddress("sender@example.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, user.getEmail());
            message.setSubject("Test email reset hesla pro " + user.getEmail());

            String htmlContent = "<h1>Reset hesla</h1>" +
                    "<p>Token pro reset hesla je " + token + " a platí do " + resetToken.getValidTo() + "</p>";
            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }
}