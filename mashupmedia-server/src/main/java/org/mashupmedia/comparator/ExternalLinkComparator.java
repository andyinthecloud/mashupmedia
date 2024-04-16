package org.mashupmedia.comparator;

import java.util.Comparator;

import org.mashupmedia.model.media.ExternalLink;

public class ExternalLinkComparator implements Comparator<ExternalLink> {

    @Override
    public int compare(ExternalLink o1, ExternalLink o2) {
        return Integer.valueOf(o1.getRank()).compareTo(Integer.valueOf(o2.getRank()));
    }

}
