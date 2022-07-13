package org.acme;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class K8sConfiguration {

    @Produces
    @IfBuildProfile("multitenant")
    public K8sClient multitenantK8sClient(KubernetesClient kubernetesClient) {
        return new MultitenantK8sClient(kubernetesClient);
    }

    @Produces
    @DefaultBean
    public K8sClient defaultK8sClient(KubernetesClient kubernetesClient) {
        return new DefaultK8sClient(kubernetesClient);
    }

}
