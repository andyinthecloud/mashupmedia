package org.mashupmedia.model.media;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.mashupmedia.model.User;

@Entity
@Table(name = "comments")
@Cacheable
public class Comment implements Serializable {
	private static final long serialVersionUID = 1552265448903497284L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String text;
	@ManyToOne(cascade = { CascadeType.PERSIST })
	private User createdBy;
	private Date createdOn;
	private Date updatedOn;
	@ManyToOne(cascade = { CascadeType.PERSIST })
	private Comment replyToComment;

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

	public Comment getReplyToComment() {
		return replyToComment;
	}

	public void setReplyToComment(Comment replyToComment) {
		this.replyToComment = replyToComment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Comment other = (Comment) obj;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (id != other.id)
			return false;
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
		builder.append(id);
		builder.append(", text=");
		builder.append(text);
		builder.append(", createdBy=");
		builder.append(createdBy);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", updatedOn=");
		builder.append(updatedOn);
		builder.append(", replyToComment=");
		builder.append(replyToComment);
		builder.append("]");
		return builder.toString();
	}

}
