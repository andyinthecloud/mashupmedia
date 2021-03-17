package org.mashupmedia.model;

import org.mashupmedia.util.MessageHelper;

public abstract class Translation {
	public abstract String getIdName();

	protected abstract String getName();

	public String getMessageKey() {
		String messageKey = MessageHelper.getMessage(getIdName(), getName());
		return messageKey;
	}

	public String getTranslatedName() {
		String messageKey = getMessageKey();
		String message = MessageHelper.getMessage(messageKey, getName());
		return message;
	}
	
	

}
