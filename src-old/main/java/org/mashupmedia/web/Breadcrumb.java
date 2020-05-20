package org.mashupmedia.web;

public class Breadcrumb {
	private String name;
	private String link;

	public Breadcrumb() {
	}

	public Breadcrumb(String name, String link) {
		this.name = name;
		this.link = link;
	}

	public Breadcrumb(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Breadcrumb [name=");
		builder.append(name);
		builder.append(", link=");
		builder.append(link);
		builder.append("]");
		return builder.toString();
	}

}
