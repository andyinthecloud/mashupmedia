package org.mashupmedia.web.proxy;

import org.mashupmedia.util.StringHelper;


public class ProxyTextFile extends ProxyFile {

	private String text;

	public ProxyTextFile(byte[] bytes) {
		super(bytes);
		this.text = StringHelper.convertFromBytes(bytes);

	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProxyTextFile [text=");
		builder.append(text);
		builder.append("]");
		return builder.toString();
	}

}
