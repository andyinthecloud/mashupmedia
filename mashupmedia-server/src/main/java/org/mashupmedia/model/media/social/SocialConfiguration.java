package org.mashupmedia.model.media.social;

import java.util.List;
import java.util.Set;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
    private boolean enableVotes;
    @EqualsAndHashCode.Include
    private boolean enableComments;
    @OneToMany(mappedBy = "socialConfiguration", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Vote> votes;
    @OneToMany(mappedBy = "socialConfiguration", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("updatedOn")
    @ToString.Exclude
    private List<Comment> comments;

}
