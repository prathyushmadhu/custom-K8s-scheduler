package com.k8scheduler.K8s_Scheduler.admission;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kubernetes.client.openapi.models.V1AdmissionRequest;
import io.kubernetes.client.openapi.models.V1AdmissionResponse;
import io.kubernetes.client.openapi.models.V1AdmissionReview;
import io.kubernetes.client.util.Yaml;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/mutate")
public class AdmissionController {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @PostMapping
    public String mutate(@RequestBody String requestBody) {
        try {
            // Parse the incoming request
            V1AdmissionReview admissionReview = Yaml.loadAs(requestBody, V1AdmissionReview.class);
            V1AdmissionRequest admissionRequest = admissionReview.getRequest();

            if (admissionRequest == null || !"Pod".equals(admissionRequest.getKind().getKind())) {
                return createResponse(admissionRequest, null); // No mutation
            }

            JsonNode podJson = mapper.readTree(admissionRequest.getObject().toString());
            ((ObjectNode) podJson.get("spec")).put("schedulerName", "custom-scheduler");

            // Create a patch
            String patch = "[{\"op\": \"replace\", \"path\": \"/spec/schedulerName\", \"value\": \"custom-scheduler\"}]";
            String base64Patch = Base64.getEncoder().encodeToString(patch.getBytes());

            return createResponse(admissionRequest, base64Patch);

        } catch (Exception e) {
            e.printStackTrace();
            return createResponse(null, null);
        }
    }

    private String createResponse(V1AdmissionRequest request, String patch) {
        V1AdmissionResponse response = new V1AdmissionResponse()
                .uid(request.getUid())
                .allowed(true);

        if (patch != null) {
            response.setPatchType("JSONPatch");
            response.setPatch(patch);
        }

        V1AdmissionReview reviewResponse = new V1AdmissionReview();
        reviewResponse.setResponse(response);

        return Yaml.dump(reviewResponse);
    }
}
