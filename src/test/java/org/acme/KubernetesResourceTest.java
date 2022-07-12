package org.acme;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@WithKubernetesTestServer
@QuarkusTest
public class KubernetesResourceTest {


    @KubernetesTestServer
    KubernetesServer mockServer;

    @BeforeEach
    public void before() {
        final Pod pod1 = new PodBuilder().withNewMetadata().withName("pod1").withNamespace("test").and().build();
        final Pod pod2 = new PodBuilder().withNewMetadata().withName("pod2").withNamespace("test").and().build();

        // Set up Kubernetes so that our "pretend" pods are created
        mockServer.getClient().pods().create(pod1);
        mockServer.getClient().pods().create(pod2);
    }

    @Test
    public void testInteractionWithAPIServer() {
        RestAssured.when().get("/k8s/test/pods").then()
                .body("size()", is(2));
    }

}