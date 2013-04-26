package org.mashupmedia.exception;

import org.apache.log4j.Logger;

public class MashupMediaRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 3399049728103632137L;

	private Logger logger = Logger.getLogger(getClass());

	public MashupMediaRuntimeException(String message) {
		super(message);
		logger.error(message);
	}

	public MashupMediaRuntimeException(String message, Throwable t) {
		super(message, t);
		logger.error(message, t);
	}

}
