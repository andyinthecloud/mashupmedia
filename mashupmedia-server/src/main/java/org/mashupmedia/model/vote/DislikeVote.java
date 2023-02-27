package org.mashupmedia.model.vote;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "dislike_votes")
@Cacheable
public class DislikeVote extends Vote {

	private static final long serialVersionUID = -874547388830336417L;

}
