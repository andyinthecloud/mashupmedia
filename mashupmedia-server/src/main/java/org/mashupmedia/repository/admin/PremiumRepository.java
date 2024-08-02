package org.mashupmedia.repository.admin;

import org.mashupmedia.model.account.Premium;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PremiumRepository extends JpaRepository<Premium, Long>{
    Premium findByNameIgnoreCase(String name);
}
