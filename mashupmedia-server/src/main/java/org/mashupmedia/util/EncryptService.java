package org.mashupmedia.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.mashupmedia.exception.MashupMediaRuntimeException;
import org.springframework.stereotype.Service;

@Service
public class EncryptService {

	private static String KEY_GENERATOR_ALGORITHM = "AES";
	private static int KEY_GENERATOR_SIZE = 192;

	private static String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
	private SecretKey secretKey;
	private IvParameterSpec iv;

	public EncryptService() {
		initialise();
	}

	private void initialise() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_GENERATOR_ALGORITHM);
			keyGenerator.init(KEY_GENERATOR_SIZE);
			this.secretKey = keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new MashupMediaRuntimeException("Error generating key", e);
		}

		this.iv = generateIv();

	}

	public static IvParameterSpec generateIv() {
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		return new IvParameterSpec(iv);
	}

	public String encrypt(String text) {
		try {
			Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte[] cipherText = cipher.doFinal(text.getBytes());
			return Base64.getEncoder()
					.encodeToString(cipherText);

		} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException
				| InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			throw new MashupMediaRuntimeException("Error encrypting", e);
		}
	}

	public String decrypt(String encryptedText){

		try {
			Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, this.secretKey, this.iv);
			byte[] plainText = cipher.doFinal(Base64.getDecoder()
					.decode(encryptedText));
			return new String(plainText);

		} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException
				| InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			throw new MashupMediaRuntimeException("Error decrypting", e);
		}
	}

}
