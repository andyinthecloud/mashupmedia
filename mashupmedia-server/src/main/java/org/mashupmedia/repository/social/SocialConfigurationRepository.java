package org.mashupmedia.repository.social;

import org.mashupmedia.model.media.social.SocialConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialConfigurationRepository extends JpaRepository<SocialConfiguration, Long> {

}
