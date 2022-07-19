package org.talend.processing.kubernetes;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "k8s")
interface K8sConfiguration {

    String jobContainerName();

    String jobConfigmap();

    String jobImagePullPolicy();

    String jobImage();

    String imagePullSecrets();

    String jobCpuRequest();

    String jobMemoryRequest();

    String jobCpu();

    String jobMemory();

    String sparkDriverXms();

    String sparkDriverXmx();

    String connectorsHostPath();

    String customHostPath();

    /* TODO replace with the lib that generated the classpath */
    String classpath();

}
