package org.acme;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/k8s")
public class KubernetesResource {

    private final KubernetesClient kubernetesClient;

    public KubernetesResource(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @GET
    @Path("/{namespace}/pods")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Pod> pods(String namespace) {
        return kubernetesClient.pods().inNamespace(namespace).list().getItems();
    }

    @GET
    @Path("/namespaces")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Namespace> namespaces() {
        return kubernetesClient.namespaces().list().getItems();
    }
}