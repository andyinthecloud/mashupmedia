package org.mashupmedia.security;

import org.mashupmedia.util.EncryptionHelper;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component("customPasswordEncoder")
public class CustomPasswordEncoder implements PasswordEncoder{

	@Override
	public String encodePassword(String rawPass, Object salt) {
		String encodedPassword = EncryptionHelper.encodePassword(rawPass);
		return encodedPassword;
	}
	
	@Override
	public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
		boolean isValid = EncryptionHelper.isPasswordValid(encPass, rawPass);
		return isValid;
	}
	
}
