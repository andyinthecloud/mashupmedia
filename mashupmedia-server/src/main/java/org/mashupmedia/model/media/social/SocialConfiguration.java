package org.mashupmedia.model.media.social;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.mashupmedia.model.account.User;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
@Table(name = "social_configurations")
@Cacheable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@ToString
@Builder
public class SocialConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "social_configuration_generator")
    @SequenceGenerator(name = "social_configurations_generator", sequenceName = "social_configurations_seq", allocationSize = 1)
    @EqualsAndHashCode.Include
    private long id;
    @EqualsAndHashCode.Include
    private boolean disableVotes;
    @EqualsAndHashCode.Include
    private boolean disableComments;
    @OneToMany(mappedBy = "socialConfiguration", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @Fetch(FetchMode.JOIN)
    private Set<Vote> votes = new HashSet<>();
    @OneToMany(mappedBy = "socialConfiguration", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("updatedOn")
    @ToString.Exclude
    private List<Comment> comments;

    public boolean isUserVotedUp(User user) {
        return isUserVoted(user, VoteType.UP_VOTE);
    }

    private boolean isUserVoted(User user, VoteType voteType) {
        return getVotes().stream()
                .filter(vote -> vote.getVoteType() == voteType)
                .anyMatch(vote -> vote.getUser().equals(user));
    }

    public void vote(User user, VoteType voteType) {
        Set<Vote> votes = getVotes();
        votes.removeIf(vote -> vote.getUser().equals(user));
        votes.add(Vote.builder()
                .createdOn(new Date())
                .user(user)
                .voteType(voteType)
                .socialConfiguration(this)
                .build());
    }

}
