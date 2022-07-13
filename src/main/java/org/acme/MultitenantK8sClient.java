package org.acme;

import io.fabric8.kubernetes.api.model.ListOptionsBuilder;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.arc.profile.IfBuildProfile;

import java.util.Collections;
import java.util.List;

public class MultitenantK8sClient implements K8sClient {

    private final KubernetesClient kubernetesClient;

    public MultitenantK8sClient(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @Override
    public Job createJob() {
        return null;
    }

    @Override
    public List<Pod> pods(String namespace) {
        return Collections.emptyList();
    }

    @Override
    public List<Namespace> namespaces() {
        return Collections.emptyList();
    }
}
