package org.talend.processing.kubernetes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

@WithKubernetesTestServer
@QuarkusTest
public class K8sClientTest {

    @Inject
    K8sConfiguration configuration;

    private K8sClient k8sClient;

    private final static String EXECUTION_ID = UUID.randomUUID().toString();
    public static final String EXPECTED_JOB_NAME = "spark-local-k8s-job-" + EXECUTION_ID;

    @KubernetesTestServer
    KubernetesServer mockServer;

    @Test
    public void createJob() {
        KubernetesClient client = mockServer.getClient();
        k8sClient = new K8sClient(configuration, client);

        String pipelineName = "sparkAppName";

        Job job = Serialization.unmarshal(K8sClientTest.class.getResourceAsStream("job.yaml"), Job.class);
        // update dynamic values
        Job expectedJob = new JobBuilder(job)
                .editMetadata().withName(EXPECTED_JOB_NAME).endMetadata()
                .editSpec().editTemplate().editMetadata().addToAnnotations("executionId", EXECUTION_ID).endMetadata().endTemplate().endSpec()
                .build();

        // name should be the same in the job.yaml file
        assertEquals(expectedJob, k8sClient.buildJob(EXECUTION_ID, pipelineName, Collections.emptyList(), Collections.emptyList(), "local[*]", "org.talend.datastreams.streamsjob.FullRunJob"));
    }

    @Test
    public void queryJobStatus() {
        k8sClient = new K8sClient(configuration, mockServer.getClient());

        mockServer.expect().withPath("/apis/v1/namespaces/test/jobs").andReturn(200, Collections.emptyMap()).once();
        mockServer.expect().withPath("/apis/v1/namespaces/test/jobs?labelSelector=job-name%3D" + EXPECTED_JOB_NAME).andReturn(200, Collections.emptyMap()).once();

        assertEquals(Optional.empty(), k8sClient.getPodStatus(EXECUTION_ID));
    }

}