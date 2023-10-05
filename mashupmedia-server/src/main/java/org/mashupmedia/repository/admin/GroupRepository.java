package org.mashupmedia.repository.admin;

import org.mashupmedia.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("select u.groups from User u where u.id = :userId")
    List<Group> findGroupsByUserId(@Param("userId") long userId);

}
