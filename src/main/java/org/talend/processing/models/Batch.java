package org.talend.processing.models;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public record Batch(String name, String proxyUser, String className, Map<String, String> conf, List<String> args,
        Path file) {
}
