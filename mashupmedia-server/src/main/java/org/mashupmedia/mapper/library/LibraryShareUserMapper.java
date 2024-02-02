package org.mashupmedia.mapper.library;

import org.mashupmedia.dto.library.LibraryShareUserPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.User;
import org.springframework.stereotype.Component;

@Component
public class LibraryShareUserMapper implements DomainMapper<User, LibraryShareUserPayload> {

    @Override
    public LibraryShareUserPayload toPayload(User domain) {
        return LibraryShareUserPayload.builder()
                .email(domain.getUsername())
                .validated(domain.isValidated())
                .build();
    }

    @Override
    public User toDomain(LibraryShareUserPayload payload) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toDomain'");
    }

}
