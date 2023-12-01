package org.mashupmedia.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    private final static String FROM = "hello@mashupmedia.org";
    private final static String ACTIVATION_SUBJECT = "Mashup Media activation code";

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
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(ACTIVATION_SUBJECT);
        simpleMailMessage.setText(String.format(body, activationCode));
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

}
