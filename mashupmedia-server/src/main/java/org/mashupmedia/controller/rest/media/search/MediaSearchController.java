package org.mashupmedia.controller.rest.media.search;

import java.util.ArrayList;
import java.util.List;

import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.dto.media.search.MediaSearchResultPayload;
import org.mashupmedia.mapper.search.AlbumMusicSearchResultPayload;
import org.mashupmedia.mapper.search.ArtistMusicSearchResultPayload;
import org.mashupmedia.mapper.search.TrackMusicSearchResultPayload;
import org.mashupmedia.model.media.SearchMediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.service.MediaManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search/media")
public class MediaSearchController {
    private final MediaManager mediaManager;
    private final TrackMusicSearchResultPayload trackMusicSearchResultPayload;
    private final AlbumMusicSearchResultPayload albumMusicSearchResultPayload;
    private final ArtistMusicSearchResultPayload artistMusicSearchResultPayload;    

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MediaSearchResultPayload>> get(@RequestParam String search) {

        List<MediaSearchResultPayload> mediaSearchResultPayloads = new ArrayList<>();

        MediaItemSearchCriteria mediaItemSearchCriteria = MediaItemSearchCriteria.builder()
                .text(search)
                .build();
        List<SearchMediaItem> searchMediaItems = mediaManager.findMediaItems(mediaItemSearchCriteria);
        for (SearchMediaItem searchMediaItem : searchMediaItems) {
            if (searchMediaItem.getResult() instanceof Track track) {
                mediaSearchResultPayloads.add(trackMusicSearchResultPayload.toPayload(track));
            } else if (searchMediaItem.getResult() instanceof Album album) {
                mediaSearchResultPayloads.add(albumMusicSearchResultPayload.toPayload(album));
            } else if (searchMediaItem.getResult() instanceof Artist artist) {
                mediaSearchResultPayloads.add(artistMusicSearchResultPayload.toPayload(artist));
            }
        }

        return ResponseEntity.ok(mediaSearchResultPayloads);

    }

}
