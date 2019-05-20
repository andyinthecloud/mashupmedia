package org.mashupmedia.util;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionHelper {

	private static Logger logger = LoggerFactory.getLogger(EncryptionHelper.class);

	private static BasicTextEncryptor textEncryptor;

	public static BasicTextEncryptor getTextEncryptor() {
		if (textEncryptor != null) {
			return textEncryptor;
		}

		textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(EncryptionHelper.class.getCanonicalName());
		return textEncryptor;
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
		if (StringUtils.isBlank(encryptedText)) {
			return encryptedText;
		}
		try {
			String text = getTextEncryptor().decrypt(encryptedText);
			return text;
		} catch (EncryptionOperationNotPossibleException e) {
			logger.info("Unable to decrypt text, perhaps it has not yet been encrypted.");
			return encryptedText;
		}
	}

}
