package org.mashupmedia.mapper.playlist;

import java.util.List;
import java.util.Optional;

import org.mashupmedia.dto.media.playlist.PlaylistPayload;
import org.mashupmedia.mapper.DomainMapper;
import org.mashupmedia.model.media.MediaItem;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.model.playlist.Playlist;
import org.mashupmedia.model.playlist.PlaylistMediaItem;
import org.springframework.stereotype.Component;

@Component
public class PlaylistMapper implements DomainMapper<Playlist, PlaylistPayload> {

    @Override
    public PlaylistPayload toDto(Playlist domain) {
        return PlaylistPayload.builder()
                .id(domain.getId())
                .name(domain.getName())
                .remainingSeconds(getRemainingSeconds(domain))
                .build();
    }

    private long getRemainingSeconds(Playlist playlist) {
        List<PlaylistMediaItem> accessiblePlaylistMediaItems = playlist.getAccessiblePlaylistMediaItems();
        if (accessiblePlaylistMediaItems == null || accessiblePlaylistMediaItems.isEmpty()) {
            return 0;
        }

        Optional<PlaylistMediaItem> currentPlaylistMediaItem = accessiblePlaylistMediaItems
                .stream()
                .filter(pmi -> pmi.isPlaying())
                .findFirst();

        int fromIndex = 0;

        if (currentPlaylistMediaItem.isPresent()) {
            fromIndex = accessiblePlaylistMediaItems.indexOf(currentPlaylistMediaItem.get());
        }

        if (fromIndex < 0) {
            fromIndex = 0;
        }

        List<PlaylistMediaItem> playlistMediaItems = accessiblePlaylistMediaItems.subList(
                fromIndex,
                accessiblePlaylistMediaItems.size() - 1);

        long remainingSeconds = 0;

        for (PlaylistMediaItem pmi : playlistMediaItems) {
            MediaItem mediaItem = pmi.getMediaItem();
            if (mediaItem instanceof Track) {
                Track track = (Track) mediaItem;
                remainingSeconds += track.getTrackLength();
            }
        }

        return remainingSeconds;
    }

    @Override
    public Playlist toDomain(PlaylistPayload payload) {
        return null;
    }

}
