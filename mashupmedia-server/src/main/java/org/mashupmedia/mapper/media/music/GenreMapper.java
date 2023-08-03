package org.mashupmedia.mapper.media.music;

import org.mashupmedia.dto.media.music.GenrePayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.music.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper implements DomainMapper<Genre, GenrePayload>{

    @Override
    public GenrePayload toDto(Genre domain) {
        return GenrePayload.builder()
        .id(domain.getId())
        .idName(domain.getName())    
        .build();
    }

    @Override
    public Genre toDomain(GenrePayload payload) {
        return null;
    }
    
}
