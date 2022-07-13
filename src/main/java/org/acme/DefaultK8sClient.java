package org.acme;

import java.util.Collections;
import java.util.List;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class DefaultK8sClient implements K8sClient {

    public static final Logger logger = LoggerFactory.getLogger(DefaultK8sClient.class);

    private final KubernetesClient kubernetesClient;

    public DefaultK8sClient(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @ConfigProperty(name = "job.vault-token-volume-name", defaultValue = "vault-token-secret-volume")
    String jobPodVaultTokenVolumeMountName;

    @ConfigProperty(name = "job.vault-token-volume-path", defaultValue = "/vault_token")
    String jobPodVaultTokenVolumeMountPath;

    @ConfigProperty(name = "job.token-secret-name", defaultValue = "remote-engine-shared-vault-secret")
    String vaultTokenSecretName;

    @PostConstruct
    public void init() {
        logger.info("Job Configurations: {}, {}, {}", jobPodVaultTokenVolumeMountName, jobPodVaultTokenVolumeMountPath, vaultTokenSecretName);
    }

    @Override
    public Job createJob() {
        return new JobBuilder()
                .editSpec()
                .editTemplate()
                .editSpec()
                .addNewVolume()
                .withName(jobPodVaultTokenVolumeMountName)
                .withNewSecret()
                .withSecretName(vaultTokenSecretName)
                .endSecret()
                .endVolume()
                .editFirstContainer()
                .addNewVolumeMount()
                .withName(jobPodVaultTokenVolumeMountName)
                .withMountPath(jobPodVaultTokenVolumeMountPath)
                .withReadOnly(false)
                .endVolumeMount()
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();
    }

    @Override
    public List<Pod> pods(String namespace) {
        return kubernetesClient.pods().inNamespace(namespace).list().getItems();
    }

    @Override
    public List<Namespace> namespaces() {
        return kubernetesClient.namespaces().list().getItems();
    }

}
