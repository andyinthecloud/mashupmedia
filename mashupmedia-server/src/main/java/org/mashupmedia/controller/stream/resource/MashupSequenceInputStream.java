package org.mashupmedia.controller.stream.resource;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;

public class MashupSequenceInputStream extends SequenceInputStream {

    public MashupSequenceInputStream(Enumeration<? extends InputStream> e) {
        super(e);
        
    }



    
    
}
