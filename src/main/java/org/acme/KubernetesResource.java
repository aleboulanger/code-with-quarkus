package org.acme;

import io.fabric8.kubernetes.api.model.ListOptionsBuilder;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Path("/k8s")
public class KubernetesResource {

    private final K8sClient k8sClient;

    public KubernetesResource(K8sClient k8sClient) {
        this.k8sClient = k8sClient;
    }

    @GET
    @Path("/{namespace}/pods")
    @Produces(MediaType.APPLICATION_JSON)
    // blocking
    public List<Pod> pods(String namespace) {
        return k8sClient.pods(namespace);
    }

    @GET
    @Path("/namespaces")
    @Produces(MediaType.APPLICATION_JSON)
    // non blocking
    public Uni<List<Namespace>> namespaces() {
        return Uni.createFrom() //
                .item(k8sClient::namespaces) //
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .ifNoItem().after(Duration.ofMillis(500))
                .recoverWithItem(Collections.emptyList());
    }
}