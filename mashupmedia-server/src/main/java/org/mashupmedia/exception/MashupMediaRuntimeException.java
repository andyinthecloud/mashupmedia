package org.mashupmedia.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MashupMediaRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 3399049728103632137L;


	public MashupMediaRuntimeException(String message) {
		super(message);
		log.error(message);
	}

	public MashupMediaRuntimeException(String message, Throwable t) {
		super(message, t);
		log.error(message, t);
	}

}
