package org.mashupmedia.model.account;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "premiums")
@Cacheable
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Premium {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "premiums_generator")
    @SequenceGenerator(name = "premiums_generator", sequenceName = "premiums_seq", allocationSize = 1)
    private long id;
    private String name;
    private long sizeInBytes;
}
