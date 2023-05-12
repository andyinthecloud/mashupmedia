package org.mashupmedia.util;

import org.mashupmedia.dto.share.NameValuePayload;

public class JsonHelper {

    public static NameValuePayload<String> createDefaultNameValueSuccessMessage() {
        return NameValuePayload.<String>builder()
                .name("message")
                .value(ValidationUtil.DEFAULT_OK_RESPONSE_MESSAGE)
                .build();
    }

}
