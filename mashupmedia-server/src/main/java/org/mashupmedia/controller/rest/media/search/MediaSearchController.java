package org.mashupmedia.controller.rest.media.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.constants.MashupMediaType;
import org.mashupmedia.dto.media.music.GenrePayload;
import org.mashupmedia.dto.media.search.MediaSearchResultPayload;
import org.mashupmedia.dto.share.NameValuePayload;
import org.mashupmedia.dto.share.PagePayload;
import org.mashupmedia.mapper.media.music.GenreMapper;
import org.mashupmedia.mapper.search.MusicSearchResultPagePayloadMapper;
import org.mashupmedia.model.media.MediaItemSearchCriteria;
import org.mashupmedia.model.media.music.Track;
import org.mashupmedia.service.MediaManager;
import org.mashupmedia.service.MusicManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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

    private final static int DEFAULT_PAGE_SIZE = 50;

    private final MediaManager mediaManager;
    private final MusicManager musicManager;
    private final GenreMapper genreMapper;
    private final MusicSearchResultPagePayloadMapper musicSearchResultPagePayloadMapper;

    @GetMapping(value = "/genres", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GenrePayload>> getGenres() {
        return ResponseEntity.ok(
                musicManager.getGenres()
                        .stream()
                        .map(genreMapper::toDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping(value = "/orderByNames", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NameValuePayload<String>>> getOrderByValues() {

        List<NameValuePayload<String>> orderByPayloads = new ArrayList<>();
        orderByPayloads.add(
                NameValuePayload
                        .<String>builder()
                        .name("Title")
                        .value("title")
                        .build());
        orderByPayloads.add(
                NameValuePayload
                        .<String>builder()
                        .name("Date added")
                        .value("createdOn")
                        .build());

        return ResponseEntity.ok(orderByPayloads);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagePayload<MediaSearchResultPayload>> get(
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) List<Integer> decades,
            @RequestParam(required = false) List<String> genreIdNames,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false) String orderBy) {

        MediaItemSearchCriteria mediaItemSearchCriteria = MediaItemSearchCriteria.builder()
                .searchText(searchText)
                .decades(decades == null ? new ArrayList<>() : decades)
                .genreIdNames(genreIdNames == null ? new ArrayList<>() : genreIdNames)
                .mashupMediaType(MashupMediaType.MUSIC)
                .build();

        Pageable pageable = getPageable(pageNumber, pageSize, orderBy);
        MashupMediaType mashupMediaType = mediaItemSearchCriteria.getMashupMediaType();
        PagePayload<MediaSearchResultPayload> pagePayload = null;

        if (mashupMediaType == MashupMediaType.MUSIC) {
            Page<Track> pageTracks = mediaManager.findMusic(mediaItemSearchCriteria, pageable);
            pagePayload = musicSearchResultPagePayloadMapper.toPayload(pageTracks);

            // if (pageTracks.getTotalElements() < pageTracks.getSize()) {
            //     Pageable pageableWitoutSort = getPageable(pageNumber, pageSize, null); 
            //     Page<Track> pageTracksWithoutSort = mediaManager.findMusic(mediaItemSearchCriteria, pageableWitoutSort);
            //     pagePayload = pagePayload.toBuilder()
            //             .totalElements(pageTracksWithoutSort.getTotalElements())
            //             .totalPages(pageTracksWithoutSort.getTotalPages())
            //             .build();
            // }

        }

        return ResponseEntity.ok(pagePayload);
    }

    private Pageable getPageable(Integer page, Integer pageSize, String orderBy) {

        int preparedPageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;

        if (StringUtils.isNotBlank(orderBy)) {
            String[] orderByParameters = orderBy.split("_");
            String name = getSortName(orderByParameters[0]);
            Direction direction = getSortDirection(orderByParameters.length == 2 ? orderByParameters[1] : null);
            return PageRequest.of(page, preparedPageSize, direction, name);
        }

        return PageRequest.of(page, preparedPageSize);
    }

    private Direction getSortDirection(String direction) {
        String preparedDirection = StringUtils.trimToEmpty(direction);

        if (StringUtils.isEmpty(preparedDirection)) {
            return Direction.ASC;
        }

        return Direction.fromOptionalString(preparedDirection).orElse(Direction.ASC);
    }

    private String getSortName(String sortBy) {
        String preparedSortBy = StringUtils.trimToEmpty(sortBy);
        return StringUtils.isEmpty(preparedSortBy) ? null : preparedSortBy;
    }

}
