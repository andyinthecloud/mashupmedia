package org.mashupmedia.comparator;

import java.util.Comparator;

import org.mashupmedia.model.media.music.Genre;
import org.mashupmedia.util.GenreHelper;

public class GenreComparator implements Comparator<Genre>{

    @Override
    public int compare(Genre o1, Genre o2) {        
        if (o1.getIdName().equals(GenreHelper.GenreType.OTHER.name())) {
            return 1;
        }

        return o1.getName().compareTo(o2.getName());
    }

}
