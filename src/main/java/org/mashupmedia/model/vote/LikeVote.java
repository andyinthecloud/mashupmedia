package org.mashupmedia.model.vote;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "like_votes")
@Cacheable
public class LikeVote extends Vote{
	private static final long serialVersionUID = -4291889373816521615L;
}
