package org.mashupmedia.service;

public interface EmailService {
 
    void sendUserActivationMail(String email, String activationCode);

    void sendUserResetPasswordMail(String email, String activationCode);

    void sendAddLibraryShareEmail(String ownerName, String email);

    void sendUserCreatedEmail(String ownerName, String email);
}
