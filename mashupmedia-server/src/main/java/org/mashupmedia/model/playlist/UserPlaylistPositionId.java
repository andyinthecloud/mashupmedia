package org.mashupmedia.model.playlist;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPlaylistPositionId implements Serializable {
    private long userId;
    private long playlistId;
}
