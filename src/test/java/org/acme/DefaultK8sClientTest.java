package org.acme;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@WithKubernetesTestServer
@QuarkusTest
@Disabled
class DefaultK8sClientTest {

    @Inject
    KubernetesClient kubernetesClient;

    @Test
    void createJob() {
        Job job = new DefaultK8sClient(kubernetesClient).createJob();
        assertNotNull(job);
    }
}