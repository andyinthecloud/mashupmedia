package org.mashupmedia.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapperHelper {


    public static <T> T[] toArray(List<T> l) {

        @SuppressWarnings("unchecked")
        T[] a = (T[]) new Object[0];        

        if (l == null || l.isEmpty()) {
            return a;
        }

        return l.toArray(a);
    }

    public static <T> List<T> toList(T[] a) {
        if (a == null || a.length == 0) {
            return new ArrayList<T>();
        }

        return Arrays.asList(a);
    }



}
