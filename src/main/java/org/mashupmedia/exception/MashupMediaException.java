package org.mashupmedia.exception;

import org.apache.log4j.Logger;

public class MashupMediaException extends RuntimeException {

	private static final long serialVersionUID = 3399049728103632137L;

	private Logger logger = Logger.getLogger(getClass());

	public MashupMediaException(String message) {
		super(message);
		logger.error(message);
	}

	public MashupMediaException(String message, Throwable t) {
		super(message, t);
		logger.error(message, t);
	}

}
