package org.mashupmedia.model;

public abstract class MetaEntity {

    abstract public long getId();

    abstract public void setRank(int rank);

    abstract public int getRank();

    public abstract void updateValues(MetaEntity updatedEntity);

}
