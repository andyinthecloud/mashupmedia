package org.mashupmedia.util;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.dto.share.ErrorCode;
import org.springframework.validation.Errors;

public class ActivationTokenUtils {

    private final static long ACTIVATION_CODE_LENGTH = 6;
    private final static long MINIMUM_TOKEN_AGE_SECONDS = 3;
    private final static long MAXIMUM_TOKEN_AGE_SECONDS = 300;
    public  final static String FIELD_NAME_ACTIVATION_CODE = "activationCode";
    public final static String ERROR_TOKEN = "Invalid token";
    private final static String ERROR_ACTIVATION_CODE_EXPIRED = "Activation code has expired";

    public static String generateActivationCode() {
        StringBuilder activationCodeBuilder = new StringBuilder();
        for (int i = 0; i < ACTIVATION_CODE_LENGTH; i++) {
            activationCodeBuilder.append(ThreadLocalRandom.current().nextInt(0, 10));
        }
        return activationCodeBuilder.toString();
    }

    public static String generateRawToken(String username, String activationCode) {
        StringBuilder rawTokenBuilder = new StringBuilder();
        rawTokenBuilder.append(username);
        rawTokenBuilder.append(",");
        rawTokenBuilder.append(activationCode);
        rawTokenBuilder.append(",");
        rawTokenBuilder.append(String.valueOf(System.currentTimeMillis()));
        return rawTokenBuilder.toString();
    }

    public static void validateToken(String decryptedToken, String username, String activationCode, Errors errors) {
        String trimmedToken = StringUtils.trimToEmpty(decryptedToken);

        if (StringUtils.isEmpty(trimmedToken)) {
            errors.rejectValue(FIELD_NAME_ACTIVATION_CODE,
                    ErrorCode.TOKEN_INVALID.getErrorCode(),
                    ERROR_TOKEN);
            return;
        }

        try {
            // String unencryptedToken = encryptService.decrypt(trimmedToken);
            String[] tokenParts = trimmedToken.split(",");
            if (tokenParts.length != 3) {
                errors.rejectValue(FIELD_NAME_ACTIVATION_CODE,
                        ErrorCode.TOKEN_INVALID.getErrorCode(),
                        ERROR_TOKEN);
                return;
            }

            if (!tokenParts[0].equals(username)) {
                errors.rejectValue(FIELD_NAME_ACTIVATION_CODE,
                        ErrorCode.TOKEN_INVALID.getErrorCode(),
                        ERROR_TOKEN);
                return;
            }

            if (!tokenParts[1].equals(activationCode)) {
                errors.rejectValue(FIELD_NAME_ACTIVATION_CODE,
                        ErrorCode.TOKEN_INVALID.getErrorCode(),
                        ERROR_TOKEN);
                return;
            }

            long timeStamp = Long.parseLong(tokenParts[2]);
            Date tokenDate = new Date(timeStamp);
            Date date = new Date(System.currentTimeMillis());

            long seconds = (date.getTime() - tokenDate.getTime()) / 1000;
            if (seconds < MINIMUM_TOKEN_AGE_SECONDS || seconds > MAXIMUM_TOKEN_AGE_SECONDS) {
                errors.rejectValue(FIELD_NAME_ACTIVATION_CODE,
                        ErrorCode.TOKEN_INVALID.getErrorCode(),
                        ERROR_ACTIVATION_CODE_EXPIRED);
            }

        } catch (NumberFormatException e) {
            errors.rejectValue(FIELD_NAME_ACTIVATION_CODE,
                    ErrorCode.TOKEN_INVALID.getErrorCode(),
                    ERROR_ACTIVATION_CODE_EXPIRED);
        }
    }
}
