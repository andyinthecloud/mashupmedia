package org.mashupmedia.exception;

import org.mashupmedia.util.MessageHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MediaItemTranscodeException extends Exception {

	private static final long serialVersionUID = -7174329107038461455L;
	
	private EncodeExceptionType encodeExceptionType;

	public enum EncodeExceptionType {
		ENCODER_NOT_CONFIGURED, UNABLE_TO_DELETE_TEMPORARY_FILE, UNSUPPORTED_ENCODING_FORMAT
	}

	public MediaItemTranscodeException(EncodeExceptionType encodeExceptionType, String messageKey) {
		super(messageKey);		
		initialise(encodeExceptionType);
	}

	public MediaItemTranscodeException(EncodeExceptionType encodeExceptionType, String messageKey, Throwable cause) {
		super(messageKey, cause);		
		initialise(encodeExceptionType);
	}

	private void initialise(EncodeExceptionType encodeExceptionType) {
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
