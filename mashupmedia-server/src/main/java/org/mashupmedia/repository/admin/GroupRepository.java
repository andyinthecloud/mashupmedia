package org.mashupmedia.repository.admin;

import org.mashupmedia.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("select u.groups from User u where u.id = :userId")
    List<Group> findGroupsByUserId(@Param("userId") long userId);

}
