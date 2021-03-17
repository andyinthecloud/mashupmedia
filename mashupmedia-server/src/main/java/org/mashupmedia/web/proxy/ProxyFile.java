package org.mashupmedia.web.proxy;

import java.util.Arrays;
import java.util.Date;


public class ProxyFile {
	private byte[] bytes;
	private Date date;

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ProxyFile(byte[] bytes) {
		this.bytes = bytes;
		this.date = new Date();
	}

	public void processBytes() {
		// override if necessary
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bytes);
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProxyFile other = (ProxyFile) obj;
		if (!Arrays.equals(bytes, other.bytes))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProxyFile [bytes=");
		builder.append(Arrays.toString(bytes));
		builder.append(", date=");
		builder.append(date);
		builder.append("]");
		return builder.toString();
	}

}
