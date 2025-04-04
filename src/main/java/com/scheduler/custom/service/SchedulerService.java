package com.scheduler.custom.service;

import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class SchedulerService {

    private final CoreV1Api api;

    // constructor [client ; api object]
    public SchedulerService() throws Exception {
        ApiClient client = Config.defaultClient();
        api = new CoreV1Api(client);
    }   

    public void schedulePods() {
        try {
            V1PodList podList = api.listPodForAllNamespaces(null, null, null, "status.phase=Pending", null, null, null, null, false, null, null);
            for (V1Pod pod : podList.getItems()) {
                log.info("Found pending pod: {}", pod.getMetadata().getName());

                // custom schedule logic implementation
                String nodeName = "minikube-m03";
                String patchJson = "[{\"op\": \"replace\", \"path\": \"/spec/nodeName\", \"value\": \"" + nodeName + "\"}]";
                V1Patch patch = new V1Patch(patchJson);

                // Apply the patch
                api.patchNamespacedPod(pod.getMetadata().getName(), 
                                       pod.getMetadata().getNamespace(), 
                                       patch, 
                                       null, null, null, null, null);

                log.info("Scheduled pod {} to node {}", pod.getMetadata().getName(), nodeName);
            }

        } catch (Exception e) {
            log.error("Error scheduling pods", e);
        }
    }
}