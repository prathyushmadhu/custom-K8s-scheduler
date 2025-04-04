package com.scheduler.custom.admission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scheduler.custom.service.SchedulerService;   
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
public class AdmissionReviewController {

    private static final Logger logger = LoggerFactory.getLogger(AdmissionReviewController.class);
    private final SchedulerService schedulerService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/mutate")
    public Mono<ObjectNode> admissionReviewRequest(@RequestBody ObjectNode request) {
        logger.info("Received AdmissionReview request: {}", request);

        return Mono.fromSupplier(() -> {
            schedulerService.schedulePods();

            // fixed component
            String uid = request.path("request").path("uid").asText();
            ObjectNode response = objectMapper.createObjectNode();
            response.put("apiVersion", "admission.k8s.io/v1");
            response.put("kind", "AdmissionReview");

            // fixed component
            ObjectNode admissionResponse = objectMapper.createObjectNode();
            admissionResponse.put("uid", uid);
            admissionResponse.put("allowed", true); 
            
            // Correctly format the patch as a JSON array
            String patchStr = "[{\"op\": \"add\", \"path\": \"/spec/schedulerName\", \"value\": \"custom-scheduler\"}]";
            String base64Patch = Base64.getEncoder().encodeToString(patchStr.getBytes(StandardCharsets.UTF_8));

            admissionResponse.put("patch", base64Patch);
            admissionResponse.put("patchType", "JSONPatch");

            response.set("response", admissionResponse);

            logger.info("Sending AdmissionReview response: {}", response);

            return response;
        });
    }
}   

