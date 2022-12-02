package org.mashupmedia.util;

import java.time.Duration;

public class TimeHelper {
    
    public enum TimeUnit {
        MINUTE, SECOND
    }

    public static int getDurationUnit(long seconds, TimeUnit timeUnit) {
        Duration duration = Duration.ofSeconds(seconds);
        
        if (timeUnit == TimeUnit.MINUTE) {
            return duration.toMinutesPart();
        } else {
            return duration.toSecondsPart();
        }
    }
}
