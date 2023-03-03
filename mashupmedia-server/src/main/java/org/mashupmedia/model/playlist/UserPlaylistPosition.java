package org.mashupmedia.model.playlist;

import org.mashupmedia.model.User;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_playlist_positions")
@Cacheable
@Data
@Builder(toBuilder = true)
@IdClass(UserPlaylistPositionId.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserPlaylistPosition {
    
    @Id
    @Column(name = "user_id")
    @EqualsAndHashCode.Include
    private long userId;

    @Id
    @Column(name = "playlist_id")
    @EqualsAndHashCode.Include
    private long playlistId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @MapsId("playlistId")
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;
    
    private long playlistMediaId;
}
