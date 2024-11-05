package org.mashupmedia.repository.media.music;

import org.mashupmedia.model.library.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long>{

    

}
