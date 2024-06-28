package org.mashupmedia.model.media.social;

import java.util.Date;

import org.mashupmedia.model.account.User;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "votes")
@Cacheable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vote_generator")
    @SequenceGenerator(name = "votes_generator", sequenceName = "votes_seq", allocationSize = 1)
    private long id;
    private Date createdOn;
    @ManyToOne
	private User user;
    @ManyToOne
	private SocialConfiguration socialConfiguration;
    @Enumerated(EnumType.STRING)
    private VoteType voteType;
}
