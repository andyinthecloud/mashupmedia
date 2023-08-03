package org.mashupmedia.repository.media.music;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.model.Group;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.media.Year;
import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.model.media.music.Track;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;

public class TrackSpecifications {

    public static Specification<Track> hasTitleLike(String searchText) {

        String processedSearchText = StringUtils.isNotBlank(searchText)
                ? "%" + searchText.toUpperCase() + "%"
                : null;

        return (root, query, builder) -> Objects.isNull(processedSearchText)
                ? builder.conjunction()
                : builder.like(
                        builder.upper(root.get("title")),
                        processedSearchText,
                        '\\');

    }

    public static Specification<Track> hasFirstNameLike(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.<String>get("title"), "%" + name + "%");
    }

    public static Specification<Track> hasGroup(List<Long> groupIds) {
        return (root, query, criteriaBuilder) -> {
            if (groupIds == null || groupIds.isEmpty()) {
                return criteriaBuilder.disjunction();
            } else {
                Join<Library, Group> librariesGroup = root.join("library").join("groups");
                return librariesGroup.get("id").in(groupIds);
            }
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

                decimalFormat.format(0, null, null);

                decimalFormat.setRoundingMode(RoundingMode.UP);
                int maximumYear = 19;

                decimalFormat.setRoundingMode(RoundingMode.DOWN);
                int minimumYear = Integer.parseInt(decimalFormat.format(yearTenth));

                Join<Year, Track> yearsTrack = root.join("year");
                return criteriaBuilder.between(yearsTrack.get("year"), minimumYear, maximumYear);
            }
        };
    }

}
