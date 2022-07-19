package org.talend.processing.models;

/**
 * Represent information from kubernetes API response
 */
public record PodInfo(String startTime, String status, String duration) {
}
