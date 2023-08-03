package org.mashupmedia.controller.rest.media.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mashupmedia.criteria.MediaItemSearchCriteria;
import org.mashupmedia.dto.media.music.GenrePayload;
import org.mashupmedia.dto.media.search.MediaSearchResultPayload;
import org.mashupmedia.mapper.media.music.GenreMapper;
import org.mashupmedia.mapper.search.AlbumMusicSearchResultPayload;
import org.mashupmedia.mapper.search.ArtistMusicSearchResultPayload;
import org.mashupmedia.mapper.search.TrackMusicSearchResultPayload;
import org.mashupmedia.model.media.SearchMediaItem;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Artist;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
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
    private final MusicManager musicManager;
    private final TrackMusicSearchResultPayload trackMusicSearchResultPayload;
    private final AlbumMusicSearchResultPayload albumMusicSearchResultPayload;
    private final ArtistMusicSearchResultPayload artistMusicSearchResultPayload;
    private final GenreMapper genreMapper;

    @GetMapping(value = "/genres", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GenrePayload>> getGenres() {
        return ResponseEntity.ok(
                musicManager.getGenres()
                        .stream()
                        .map(genreMapper::toDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MediaSearchResultPayload>> get(
        @RequestParam(required = false) String searchText,
        @RequestParam(required = false) List<Integer> decades,
        @RequestParam(required = false) List<String> genreIdNames) {

        List<MediaSearchResultPayload> mediaSearchResultPayloads = new ArrayList<>();

        MediaItemSearchCriteria mediaItemSearchCriteria = MediaItemSearchCriteria.builder()
                .searchText(searchText)
                .decades(decades == null ? new ArrayList<>() : decades)
                .genreIdNames(genreIdNames == null ? new ArrayList<>() : genreIdNames)
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
