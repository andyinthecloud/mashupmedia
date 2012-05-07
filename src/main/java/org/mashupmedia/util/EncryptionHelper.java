package org.mashupmedia.util;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;

public class EncryptionHelper {

	private static PasswordEncryptor passwordEncryptor;
	private static BasicTextEncryptor textEncryptor;

	public static PasswordEncryptor getPasswordEncryptor() {

		if (passwordEncryptor != null) {
			return passwordEncryptor;
		}

		passwordEncryptor = new StrongPasswordEncryptor();
		return passwordEncryptor;
	}
	
	public static BasicTextEncryptor getTextEncryptor() {
		if (textEncryptor != null) {
			return textEncryptor;
		}
		
		textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(EncryptionHelper.class.getCanonicalName());
		return textEncryptor;
	}
	

	public static String encodePassword(String password) {
		String encodedPassword = getPasswordEncryptor().encryptPassword(password);
		return encodedPassword;
	}

	public static boolean isPasswordValid(String encodedPassword, String rawPassword) {
		boolean isValid = getPasswordEncryptor().checkPassword(rawPassword, encodedPassword);
		return isValid;
	}

	public static String encryptText(String text) {
		text = StringUtils.trimToEmpty(text);
		if (StringUtils.isEmpty(text)) {
			return text;
		}
		String encryptedText = getTextEncryptor().encrypt(text);		
		return encryptedText;
	}
	

	public static String decryptText(String encryptedText) {
		String text = getTextEncryptor().decrypt(encryptedText);
		return text;
	}

}
