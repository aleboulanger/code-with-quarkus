package org.talend.processing;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Arrays;
import java.util.Map;

@Path("/version")
public class VersionController {

    @GET
    public Uni<Map<String, Object>> getApplicationFeatures() {
        return Uni.createFrom().item(Map.of("version", "0.1", "apps", Arrays.asList("SparkLocal", "SemanticTypesFullRun")));
    }
}
