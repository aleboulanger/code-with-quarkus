package org.talend.processing;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.talend.processing.kubernetes.K8sClient;
import org.talend.processing.models.Batch;
import org.talend.processing.models.BatchResponse;

import java.util.Collections;

@Path("/batches")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PipelineLauncherController {

    private final K8sClient k8sClient;

    public PipelineLauncherController(K8sClient k8sClient) {
        this.k8sClient = k8sClient;
    }

    @POST
    public BatchResponse createJob(Batch batch, @QueryParam("executionId") String executionId, @QueryParam("mode") @DefaultValue("local[1]") String mode) {
        //TODO return 201 created with Location header on GET /Batches/id
        if (executionId == null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("executionID parameter is mandatory")
                            .build()
            );
        }
        k8sClient.createJob(executionId, batch.name(), Collections.emptyList(), batch.args(), mode, batch.className());
        return getPodInfo(executionId);
    }

    @GET
    @Path("/{batchId}")
    public BatchResponse getPodInfo(@PathParam("batchId") String batchId) {
        return null;
        //TODO
        // return k8sClient.getPodStatus(batchId) //
        //        .map(podInfo -> new BatchResponse(podInfo.status(), batchId, podInfo.startTime(), podInfo.duration())) //
        //        .orElseGet(() -> new BatchResponse("error", batchId,
        //                String.valueOf(System.currentTimeMillis()), "N/A"));
    }
}
