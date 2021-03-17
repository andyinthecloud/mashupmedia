package org.mashupmedia.exception;

import org.mashupmedia.util.MessageHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MediaItemEncodeException extends Exception {

	private static final long serialVersionUID = -7174329107038461455L;
	
	private EncodeExceptionType encodeExceptionType;

	public enum EncodeExceptionType {
		ENCODER_NOT_CONFIGURED, UNABLE_TO_DELETE_PREVIOUS_ENCODED_FILE, UNSUPPORTED_ENCODING_FORMAT
	}

	public MediaItemEncodeException(EncodeExceptionType encodeExceptionType, String messageKey) {
		super(messageKey);		
		log.error(getDisplayMessage());
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
