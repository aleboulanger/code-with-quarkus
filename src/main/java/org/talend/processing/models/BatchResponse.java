package org.talend.processing.models;

import java.util.Collections;
import java.util.List;
import java.util.Map;

//TODO change state to enum
// has been removed => int id, String appId,
//TODO remove appInfo and log fields
public record BatchResponse(Map<String, String> appInfo, List<String> log, String state, String additionalId, String startTime, String duration) {

    public BatchResponse(String state, String additionalId, String startTime, String duration) {
        this(Collections.emptyMap(), Collections.emptyList(), state, additionalId, startTime, duration);
    }

}
