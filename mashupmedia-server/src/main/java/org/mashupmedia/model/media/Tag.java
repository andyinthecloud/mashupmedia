package org.mashupmedia.model.media;

import java.io.Serializable;
import java.util.Date;

import org.mashupmedia.model.account.User;

import jakarta.persistence.*;

@Entity
@Table(name = "tags")
@Cacheable
public class Tag implements Serializable {

	private static final long serialVersionUID = -1056696816094274402L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tags_generator")
	@SequenceGenerator(name = "tags_generator", sequenceName = "tags_seq", allocationSize = 1)
	private long id;
	private String text;
	@ManyToOne(cascade = { CascadeType.PERSIST })
	private User createdBy;
	private Date createdOn;
	private Date updatedOn;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		Tag other = (Tag) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Tag [id=");
		builder.append(id);
		builder.append(", text=");
		builder.append(text);
		builder.append(", createdBy=");
		builder.append(createdBy);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", updatedOn=");
		builder.append(updatedOn);
		builder.append("]");
		return builder.toString();
	}

}
