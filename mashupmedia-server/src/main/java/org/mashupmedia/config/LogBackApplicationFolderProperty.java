package org.mashupmedia.config;

import ch.qos.logback.core.PropertyDefinerBase;
import org.mashupmedia.util.FileHelper;

import java.io.File;

public class LogBackApplicationFolderProperty extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        File logFolder = new File(FileHelper.getApplicationFolder(), "logs");
        return logFolder.getAbsolutePath();
    }
}
