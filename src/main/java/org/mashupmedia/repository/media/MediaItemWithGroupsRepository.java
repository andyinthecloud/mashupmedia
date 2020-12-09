package org.mashupmedia.repository.media;

import org.mashupmedia.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface MediaItemWithGroupsRepository<T, ID> extends JpaRepository<T, ID> {

    default List<Group> findGroupsById(long id) {
        throw new UnsupportedOperationException("findGroupsById not implemented");
    }
}
