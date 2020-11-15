package org.mashupmedia.security;

import org.mashupmedia.util.EncryptionHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//@Component("customPasswordEncoder")
public class CustomPasswordEncoder {

//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//	}


//	@Override
	public String encode(CharSequence rawPassword) {
//		String encodedPassword = EncryptionHelper.encodePassword(rawPassword.toString());
//		return encodedPassword;
		return "";
	}

//	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
//		boolean isValid = EncryptionHelper.isPasswordValid(encodedPassword, rawPassword.toString());
//		return isValid;
		return false;
	}

}
