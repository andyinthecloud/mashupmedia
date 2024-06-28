package org.mashupmedia.model.media.social;

import java.io.Serializable;
import java.util.Date;

import org.mashupmedia.model.account.User;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "comments")
@Cacheable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class Comment implements Serializable {
	private static final long serialVersionUID = 1552265448903497284L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_generator")
	@SequenceGenerator(name = "comments_generator", sequenceName = "comments_seq", allocationSize = 1)
	@EqualsAndHashCode.Include
	private long id;
	private String text;
	@ManyToOne(cascade = { CascadeType.PERSIST })	
	@EqualsAndHashCode.Include
	private User user;
	@ManyToOne
	@EqualsAndHashCode.Include
	private SocialConfiguration socialConfiguration;
	@EqualsAndHashCode.Include
	private Date createdOn;
	private Date updatedOn;
	@ManyToOne(cascade = { CascadeType.PERSIST })
	@ToString.Exclude
	private Comment replyToComment;

}
