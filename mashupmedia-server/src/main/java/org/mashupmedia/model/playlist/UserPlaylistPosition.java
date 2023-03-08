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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_playlist_positions")
@Cacheable
@Builder(toBuilder = true)
@IdClass(UserPlaylistPositionId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPlaylistPosition {
    
    @Id
    @Column(name = "user_id")
    private long userId;

    @Id
    @Column(name = "playlist_id")
    private long playlistId;

    @ManyToOne
    @MapsId("userId")
    private User user;
    
    @ManyToOne
    @MapsId("playlistId")
    private Playlist playlist;
    
    private long playlistMediaId;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (userId ^ (userId >>> 32));
        result = prime * result + (int) (playlistId ^ (playlistId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserPlaylistPosition other = (UserPlaylistPosition) obj;
        if (userId != other.userId)
            return false;
        if (playlistId != other.playlistId)
            return false;
        return true;
    }

    
}
