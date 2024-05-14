package org.mashupmedia.comparator;

import java.util.Comparator;

import org.mashupmedia.model.account.User;

public class UserComparator implements Comparator<User>{

    @Override
    public int compare(User o1, User o2) {
        return o1.getUsername().compareTo(o2.getUsername());
    }

}
