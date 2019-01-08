package org.mashupmedia.validator;

import org.mashupmedia.web.page.EncodingPage;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class EncodingPageValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(EncodingPage.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		EncodingPage encodingPage = (EncodingPage) target;
		int totalFfmpegProcesses = encodingPage.getTotalFfmpegProcesses();
		if (totalFfmpegProcesses < 0) {
			errors.rejectValue("totalFfmpegProcesses", "encoding.error.processes.format");
		}		
	}
	

}
