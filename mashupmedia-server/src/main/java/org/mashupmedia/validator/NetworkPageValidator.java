package org.mashupmedia.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mashupmedia.web.page.NetworkPage;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class NetworkPageValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return NetworkPage.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		NetworkPage networkPage = (NetworkPage) target;
		String proxyPortValue = StringUtils.trimToEmpty(networkPage.getProxyPort());
		if (StringUtils.isNotEmpty(proxyPortValue) && !NumberUtils.isNumber(proxyPortValue)) {
			errors.rejectValue("proxyPort", "network.proxy.port.error");
		}

	}

}
