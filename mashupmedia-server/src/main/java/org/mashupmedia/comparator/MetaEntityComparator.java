package org.mashupmedia.comparator;

import java.util.Comparator;

import org.mashupmedia.model.MetaEntity;

public class MetaEntityComparator implements Comparator<MetaEntity> {

    @Override
    public int compare(MetaEntity o1, MetaEntity o2) {
        Integer rank1 = o1.getRank();
        Integer rank2 = o2.getRank();
        return rank1.compareTo(rank2);
    }

}
