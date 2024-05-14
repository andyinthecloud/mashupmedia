package org.mashupmedia.repository.admin;

import org.mashupmedia.model.account.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    @Query("select count(u) from User u join u.roles r where r.idName = 'role.admin'")
    Long getTotalUserAdministrators();
}
