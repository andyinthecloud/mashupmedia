package org.mashupmedia.model.media;

import org.mashupmedia.model.MetaEntity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
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
@EqualsAndHashCode(callSuper = false)
@ToString
@Builder
public class ExternalLink extends MetaEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "external_links_generator")
    @SequenceGenerator(name = "external_links_generator", sequenceName = "external_links_seq", allocationSize = 1)
    private long id;
    @Size(max = 255)
    private String name;
    @Size(max = 1024)
    private String link;
    private int rank;

    // public boolean included(Collection<ExternalLink> externalLinks) {
    //     if (this.id < 1) {
    //         return false;
    //     }

    //     return externalLinks.stream()
    //             .anyMatch(externalLink -> externalLink.id == this.id);
    // }
    
    @Override
    public void updateValues(MetaEntity updatedEntity) {
        if (updatedEntity instanceof ExternalLink externalLink) {
            setLink(externalLink.getLink());
            setName(externalLink.getName());
            setRank(externalLink.getRank());    
        }
    }





    

}