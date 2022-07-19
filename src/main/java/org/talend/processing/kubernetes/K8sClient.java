package org.talend.processing.kubernetes;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.talend.processing.models.PodInfo;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class K8sClient {

    private static final Logger logger = LoggerFactory.getLogger(K8sClient.class);

    private final K8sConfiguration configuration;

    private final KubernetesClient kubernetesClient;

    public K8sClient(K8sConfiguration configuration, KubernetesClient kubernetesClient) {
        this.configuration = configuration;
        this.kubernetesClient = kubernetesClient;
        // logger.warn("Ossama ramene les pains chocs demain");
        logger.info(configuration.toString());
    }

    private String jobJavaCommand(String name, List<String> librairies, List<String> args, String sparkMaster, String className) {
        String cp = String.join(":", librairies);
        String additionalArgs = String.join(" ", args);
        return String
                .format("java -Xms%s -Xmx%s -Dspark.app.name='%s' "
                        + "-Dtalend.component.manager.m2.repository=/opt/talend/connectors "
                        + "-Dlog4j.configurationFile=/opt/spark/conf/log4j2-console-only.xml "
                        + "-Dtalend.spark.streaming.batch.interval=5000 -Dspark.master=%s "
                        + "-cp %s %s %s", configuration.sparkDriverXms(),
                        configuration.sparkDriverXmx(), name, sparkMaster, configuration.classpath(), className, additionalArgs);
    }

    public Job buildJob(String executionId, String name, List<String> librairies, List<String> args,
                        String sparkMaster, String className) {
        String jobName = configuration.jobContainerName() + "-" + executionId.replace("_", "-");
        return new JobBuilder()
                .withNewMetadata()
                .withName(jobName)
                .endMetadata()
                .withNewSpec()
                .withNewTemplate()
                .withNewMetadata()
                .addToAnnotations("pipeline-name", name)
                .addToAnnotations("executionId", executionId)
                .endMetadata()
                .withNewSpec()
                .addNewImagePullSecret(configuration.imagePullSecrets())
                .addNewVolume()
                .withName("connectors")
                .withNewHostPath(configuration.connectorsHostPath(), "Directory")
                .endVolume()
                .addNewVolume()
                .withName("custom")
                .withNewHostPath(configuration.customHostPath(), "Directory")
                .endVolume()
                .addNewContainer()
                .withName(configuration.jobContainerName())
                .withImage(configuration.jobImage())
                .withImagePullPolicy(configuration.jobImagePullPolicy())
                .withCommand("/bin/sh")
                .withArgs("-c", jobJavaCommand(name, librairies, args, sparkMaster, className))
                .addNewEnvFrom().withNewConfigMapRef().withName(configuration.jobConfigmap()).endConfigMapRef().endEnvFrom()
                .withNewResources()
                .addToLimits("cpu", new Quantity(configuration.jobCpu()))
                .addToLimits("memory", new Quantity(configuration.jobMemory()))
                .addToRequests("cpu", new Quantity(configuration.jobCpuRequest()))
                .addToRequests("memory", new Quantity(configuration.jobMemoryRequest()))
                .endResources()
                .addNewVolumeMount()
                .withName("connectors")
                .withMountPath("/opt/talend/connectors")
                .endVolumeMount()
                .endContainer()
                .withRestartPolicy("Never")
                .endSpec()
                .endTemplate()
                .withBackoffLimit(0)
                .endSpec()
                .build();
    }

    public Job createJob(String executionId, String name, List<String> librairies, List<String> args,
                         String sparkMaster, String className) {
        return kubernetesClient
                .batch()
                .jobs()
                .inNamespace(kubernetesClient.getNamespace())
                .create(buildJob(executionId, name, librairies, args, sparkMaster, className));
    }

    public Optional<PodInfo> getPodStatus(String executionId) {
        return getPod(executionId).map(pod -> {
            String startime = "N/A";
            String duration = "N/A";
            try {
                long startTimestamp = Instant.parse(Optional
                        .ofNullable(pod.getStatus()).map(status -> status.getStartTime()).orElse("0")).toEpochMilli();
                startime = String.valueOf(startTimestamp);
                Optional<String> maybeFinishedTime = getFinishedTime(executionId);
                duration = maybeFinishedTime.map(finishedTime -> {
                    long dur = Instant.parse(finishedTime).toEpochMilli() - startTimestamp;
                    return String.valueOf(dur);
                }).orElse("N/A");
            } catch (DateTimeParseException e) {
                logger.warn("Unable to parse the kubernetes job pod startTime ({})", executionId, e);
            }

            String status = k8sPodStatusToBatchState(pod.getStatus().getPhase());
            return new PodInfo(startime, status, duration);
        });
    }

    private Optional<String> getFinishedTime(String executionId) {
        return getPod(executionId)
                .get()
                .getStatus()
                .getContainerStatuses()
                .stream()
                .filter(containerStatus ->
                        configuration.jobContainerName().equals(containerStatus.getName()) &&
                                containerStatus.getState().getTerminated() != null)
                .map(containerStatus -> containerStatus.getState().getTerminated().getFinishedAt())
                .findFirst();
    }

    private Optional<Pod> getPod(String executionId) {
        String jobName = configuration.jobContainerName() + "-" + executionId.replace("_", "-");
        return kubernetesClient
                .pods()
                .inNamespace(kubernetesClient.getNamespace()) //TODO check if inNamespace is required here
                .withLabel("job-name", jobName)
                .list()
                .getItems()
                .stream()
                .findFirst();
    }

    // TODO create and bind with an enum
    private String k8sPodStatusToBatchState(String podStatus) {
        return switch (podStatus) {
            case "Pending" -> "starting";
            case "Running" -> "running";
            case "Succeeded" -> "success";
            case "Failed" -> "error";
            default -> "idle";
        };
    }
}
