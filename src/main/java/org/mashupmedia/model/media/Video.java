package org.mashupmedia.model.media;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
@Cacheable
@XmlRootElement
public class Video extends MediaItem{
	private static final long serialVersionUID = 8105872585865313104L;

}
