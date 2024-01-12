package org.mashupmedia.service;

import org.mashupmedia.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final AdminManager adminManager;

    private final static String FROM = "hello@mashupmedia.org";
    private final static String ACTIVATION_SUBJECT = "Mashup Media activation code";

    @Value("${server.servlet.host}")
    private String host;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    private final String ACTIVATE_USER_URI = "activate-user"; 

    @Override
    public void sendUserActivationMail(String email, String activationCode) {

        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Welcome to Mashup Media!");
        textBuilder.append("\r\n\r\n");
        textBuilder.append("You account activation code is %s.");
        textBuilder.append("\r\n\r\n");
        textBuilder.append("Please note the code is only valid for 5 minutes.");
        textBuilder.append("\r\n\r\n");
        textBuilder.append("Thank you,");
        textBuilder.append("\r\n\r\n");
        textBuilder.append("The Mashup Media team");

        sendEmail(email, activationCode, textBuilder.toString());
    }

    private void sendEmail(String email, String activationCode, String body) {
        String processedBody = String.format(body, activationCode);
        sendEmail(email, processedBody);
    }

    private void sendEmail(String email, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(ACTIVATION_SUBJECT);
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public void sendUserResetPasswordMail(String email, String activationCode) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("You have requested to reset your Mashup Media password!");
        textBuilder.append("\r\n\r\n");
        textBuilder.append("The activation code is %s.");
        textBuilder.append("\r\n\r\n");
        textBuilder.append("Please note the code is only valid for 5 minutes.");
        textBuilder.append("\r\n\r\n");
        textBuilder.append("Thank you,");
        textBuilder.append("\r\n\r\n");
        textBuilder.append("The Mashup Media team");

        sendEmail(email, activationCode, textBuilder.toString());
    }

    @Override
    public void sendAddLibraryShareEmail(String ownerName, String email) {

        // Only send mail for new or unvalidated users
        User user = adminManager.getUser(email);
        if (user != null && user.isValidated()) {
            return;
        }

        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append(String.format("%s has shared their Mashup Media music library with you!", ownerName)  );
        textBuilder.append("\r\n\r\n");
        textBuilder.append("Please activate your account on ");
        textBuilder.append("\r\n");
        textBuilder.append(host + "/" + ACTIVATE_USER_URI + ".");
        textBuilder.append("\r\n\r\n");
        textBuilder.append("We look forward to seeing you soon,");
        textBuilder.append("\r\n\r\n");
        textBuilder.append("The Mashup Media team");

        sendEmail(email, textBuilder.toString());
    }

    @Override
    public void sendUserCreatedEmail(String ownerName, String email) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append(String.format("%s has registered you on Mashup Media!", ownerName)  );
        textBuilder.append("\r\n\r\n");
        textBuilder.append("Please click on the link below to activate your account:");
        textBuilder.append("\r\n");
        textBuilder.append(host + "/" + ACTIVATE_USER_URI + ".");
        textBuilder.append("\r\n\r\n");
        textBuilder.append("We look forward to seeing you soon,");
        textBuilder.append("\r\n\r\n");
        textBuilder.append("The Mashup Media team");

        sendEmail(email, textBuilder.toString());        
    }

}
