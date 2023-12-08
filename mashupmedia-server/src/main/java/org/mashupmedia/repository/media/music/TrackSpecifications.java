package org.mashupmedia.repository.media.music;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.User;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.model.media.music.Album;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Track;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import jakarta.persistence.criteria.Join;

public class TrackSpecifications {

    public static Specification<Track> hasTitleLike(String searchText) {

        String processedSearchText = StringUtils.isNotBlank(searchText)
                ? "%" + searchText.toUpperCase() + "%"
                : null;

        return (root, query, builder) -> {
            query.distinct(true);
            return Objects.isNull(processedSearchText)
                    ? builder.conjunction()
                    : builder.like(
                            builder.upper(root.get("title")),
                            processedSearchText,
                            '\\');
        };

    }

    public static Specification<Track> hasAlbumNameLike(String searchText) {

        String processedSearchText = StringUtils.isNotBlank(searchText)
                ? "%" + searchText.toUpperCase() + "%"
                : null;

        return (root, query, builder) -> {
            if (Objects.isNull(processedSearchText)) {
                return builder.conjunction();
            } else {
                Join<Library, Album> tracksAlbum = root.join("album");
                return builder.like(
                        builder.upper(tracksAlbum.get("name")),
                        processedSearchText,
                        '\\');

            }
        };
    }

    public static Specification<Track> hasArtistNameLike(String searchText) {

        String processedSearchText = StringUtils.isNotBlank(searchText)
                ? "%" + searchText.toUpperCase() + "%"
                : null;

        return (root, query, builder) -> {
            if (Objects.isNull(processedSearchText)) {
                return builder.conjunction();
            } else {
                Join<Library, Album> tracksArtist = root.join("artist");
                return builder.like(
                        builder.upper(tracksArtist.get("name")),
                        processedSearchText,
                        '\\');

            }
        };
    }

    // public static Specification<Track> hasGroup(List<Long> groupIds) {
    // return (root, query, criteriaBuilder) -> {
    // if (groupIds == null || groupIds.isEmpty()) {
    // return criteriaBuilder.disjunction();
    // } else {
    // Join<Library, Group> librariesGroup = root.join("library").join("groups");
    // return librariesGroup.get("id").in(groupIds);
    // }
    // };
    // }

    public static Specification<Track> hasUser(Long userId) {
        Assert.notNull(userId, "Expecting a user id");
        return (root, query, criteriaBuilder) -> {
            Join<Library, User> librariesUser = root.join("library").join("users");
            return criteriaBuilder.equal(librariesUser.get("id"), userId);
        };
    }

    public static Specification<Track> hasGenre(List<String> genreIdNames) {
        return (root, query, criteriaBuilder) -> {
            if (genreIdNames == null || genreIdNames.isEmpty()) {
                return criteriaBuilder.conjunction();
            } else {
                Join<Genre, Track> genresTrack = root.join("genre");
                return genresTrack.get("name").in(genreIdNames);
            }
        };
    }

    public static Specification<Track> hasDecade(Integer year) {
        return (root, query, criteriaBuilder) -> {
            if (year == null) {
                return criteriaBuilder.conjunction();
            } else {
                double yearTenth = year / 10;
                DecimalFormat decimalFormat = new DecimalFormat("#");
                decimalFormat.setRoundingMode(RoundingMode.FLOOR);
                int floor = Integer.valueOf(decimalFormat.format(yearTenth)) * 10;
                int ceiling = floor + 9;
                Join<Year, Track> yearsTrack = root.join("year");
                return criteriaBuilder.between(yearsTrack.get("year"), floor, ceiling);
            }
        };
    }

}
