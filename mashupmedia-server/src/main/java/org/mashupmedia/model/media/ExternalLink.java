package org.mashupmedia.model.media;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "external_links")
@Cacheable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ExternalLink {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String link;
    private int rank;

    public boolean included(Collection<ExternalLink> externalLinks) {
        if (this.id < 1) {
            return false;
        }

        return externalLinks.stream()
                .anyMatch(externalLink -> externalLink.id == this.id);
    }

    public void update(Collection<ExternalLink> externalLinks) {
        if (this.id < 1) {
            return;
        }

        externalLinks.stream()
                .filter(externalLink -> externalLink.id == this.id)
                .forEach(externalLink -> {
                    setLink(externalLink.getLink());
                    setName(externalLink.getName());
                    setRank(externalLink.getRank());
                });
    }

}