package org.mashupmedia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.extern.slf4j.Slf4j;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Slf4j
public class PageNotFoundException extends MashupMediaRuntimeException {

	private static final long serialVersionUID = 671985039755981747L;

	public PageNotFoundException(String message) {
		super(message);
		log.info(message);

	}

}
