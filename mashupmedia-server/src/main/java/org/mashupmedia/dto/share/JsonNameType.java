package org.mashupmedia.dto.share;

public enum JsonNameType {

    OUTPUT("output"),
    ERROR("error");

    private JsonNameType(String name) {
        this.name = name;
    }

    public final String name;

    
}
