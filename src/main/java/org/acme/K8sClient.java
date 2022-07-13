package org.acme;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.batch.v1.Job;

import java.util.List;

public interface K8sClient {

    Job createJob();

    List<Pod> pods(String namespace);

    List<Namespace> namespaces();
}
