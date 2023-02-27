package org.mashupmedia.model.vote;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "like_votes")
@Cacheable
public class LikeVote extends Vote{
	private static final long serialVersionUID = -4291889373816521615L;
}
