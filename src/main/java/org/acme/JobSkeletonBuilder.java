/*
package org.acme;

import io.fabric8.kubernetes.api.model.EnvFromSourceBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;

import java.util.Map;

public class JobSkeletonBuilder {

    private Long fsGroup;
    private Long supplementalGroups;
    private String imagePullSecretsName;
    private String restartPolicy;
    private String jobPodConnectorsVolumeMountName;
    private String connectorsHostPathPath;
    private String jobPodCustomVolumeMountName;
    private String customHostPathPath;
    private String jobContainerName;
    private String jobPodImage;

    public JobBuilder create(String jobName, String clientNamespace) {
        return new JobBuilder()
                .editOrNewMetadata()
                .withName(jobName)
                .withNamespace(clientNamespace)
                .endMetadata()
                .editOrNewSpec()
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("co.elastic.logs/enabled", "false")
                .addToAnnotations(Map
                        .of("pipeline-name", name, "executionId", executionId, "tenantId", tenantId,
                                "seccomp.security.alpha.kubernetes.io/pod", "runtime/default"))
                .endMetadata()
                .editOrNewSpec()
                .withNewSecurityContext()
                .withFsGroup(fsGroup)
                .withSupplementalGroups(supplementalGroups)
                .endSecurityContext()
                .addNewImagePullSecret()
                .withName(imagePullSecretsName)
                .endImagePullSecret()
                .withRestartPolicy(restartPolicy)
                .addNewVolume()
                .withName(jobPodConnectorsVolumeMountName)
                .editOrNewHostPath()
                .withPath(connectorsHostPathPath)
                .withType("Directory")
                .endHostPath()
                .endVolume()
                .addNewVolume()
                .withName(jobPodCustomVolumeMountName)
                .editOrNewHostPath()
                .withPath(customHostPathPath)
                .withType("Directory")
                .endHostPath()
                .endVolume()
                .addNewContainer()
                .withName(jobContainerName)
                .withImage(jobPodImage)
                .withNewSecurityContext()
                .withAllowPrivilegeEscalation(AllowPrivilegeEscalation)
                .withRunAsUser(runAsUser)
                .withRunAsGroup(runAsGroup)
                .endSecurityContext()
                .withImagePullPolicy(jobPodImagePullPolicy)
                .withCommand("/bin/sh")
                .withArgs("-c", jobJavaCommand)
                .withEnvFrom(
                        new EnvFromSourceBuilder()
                                .withNewConfigMapRef()
                                .withName(jobConfigmap)
                                .endConfigMapRef()
                                .build(),
                        new EnvFromSourceBuilder()
                                .withNewConfigMapRef()
                                .withName(jobLauncherConfigmap)
                                .endConfigMapRef()
                                .build())
                .editOrNewResources()
                .withLimits(Map
                        .of("cpu", new Quantity(jobPodResourceCPULimit), "memory",
                                new Quantity(jobPodResourceMemoryLimit)))
                .withRequests(Map
                        .of("cpu", new Quantity(jobPodResourceCPURequest), "memory",
                                new Quantity(jobPodResourceMemoryRequest)))
                .endResources()
                .addNewVolumeMount()
                .withName(jobPodConnectorsVolumeMountName)
                .withMountPath(jobPodConnectorsVolumeMountPath)
                .endVolumeMount()
                .endContainer()
                .endSpec()
                .endTemplate()
                .withBackoffLimit(0)
                .withTtlSecondsAfterFinished(ttlSecondsAfterFinished)
                .endSpec();
    }
}
*/
