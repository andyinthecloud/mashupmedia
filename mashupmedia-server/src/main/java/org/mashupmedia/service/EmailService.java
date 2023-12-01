package org.mashupmedia.service;

public interface EmailService {
 
    void sendUserActivationMail(String email, String activationCode);

    void sendUserResetPasswordMail(String email, String activationCode);

}
