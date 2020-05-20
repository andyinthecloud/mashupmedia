package org.mashupmedia.exception;

import org.apache.log4j.Logger;
import org.mashupmedia.util.MessageHelper;

public class MediaItemEncodeException extends Exception {

	private static final long serialVersionUID = -7174329107038461455L;
	
	private Logger logger = Logger.getLogger(getClass());

	private EncodeExceptionType encodeExceptionType;

	public enum EncodeExceptionType {
		ENCODER_NOT_CONFIGURED, UNABLE_TO_DELETE_PREVIOUS_ENCODED_FILE, UNSUPPORTED_ENCODING_FORMAT
	}

	public MediaItemEncodeException(EncodeExceptionType encodeExceptionType, String messageKey) {
		super(messageKey);		
		logger.error(getDisplayMessage());
		this.encodeExceptionType = encodeExceptionType;
	}

	public EncodeExceptionType getEncodeExceptionType() {
		return encodeExceptionType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MediaItemEncodeException [encodeExceptionType=");
		builder.append(encodeExceptionType.name());
		builder.append(", getMessage()=");
		builder.append(getDisplayMessage());
		builder.append("]");
		return builder.toString();
	}
	
	protected String getDisplayMessage() {
		String displayMessage = MessageHelper.getMessage(getMessage());
		if (displayMessage.equals(MessageHelper.MESSAGE_NOT_FOUND)) {
			displayMessage = getMessage();
		}
		return displayMessage;
	}
	
	

}
