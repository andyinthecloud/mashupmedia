package org.mashupmedia.exception;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class PageNotFoundException extends MashupMediaException {
	private Logger logger = Logger.getLogger(getClass());

	private static final long serialVersionUID = 671985039755981747L;

	public PageNotFoundException(String message) {
		super(message);
		logger.info(message);

	}

}
