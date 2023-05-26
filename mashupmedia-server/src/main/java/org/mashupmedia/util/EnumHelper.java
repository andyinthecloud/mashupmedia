package org.mashupmedia.util;

import java.util.EnumSet;

public class EnumHelper {

    public static <E extends Enum<E>> E getEnum(Class<E> clazz, String name) {

        return EnumSet.allOf(clazz).stream()
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findAny().orElse(null);

    }
}
