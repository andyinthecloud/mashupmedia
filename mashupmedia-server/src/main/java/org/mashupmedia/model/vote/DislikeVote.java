package org.mashupmedia.model.vote;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Entity
@Cacheable
public class DislikeVote extends Vote {

	private static final long serialVersionUID = -874547388830336417L;

}
