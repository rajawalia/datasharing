package com.example.ca;

import com.digicert.tlm.SdkContext;
import com.digicert.tlm.plugin.PluginError;
import com.digicert.tlm.plugin.PluginUtils;
import com.digicert.tlm.plugin.Response;
import com.digicert.tlm.plugin.WorkflowEntryPoint;
import com.digicert.tlm.plugin.model.PluginConfiguration;
import com.digicert.tlm.workflows.WorkflowExecutionException;
import com.digicert.tlm.workflows.ca.AbstractCaWorkflow;
import com.digicert.tlm.workflows.ca.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Sample CA plugin for a hypothetical REST CA ("Example CA"). testConnection makes a real
 * HTTP call; the other actions return placeholder data. Replace each TODO with your CA's API.
 */
@Slf4j
@WorkflowEntryPoint(name = "MyCaPlugin")
public class MyCaPlugin extends AbstractCaWorkflow {

    private final MyPluginConfiguration config;

    public MyCaPlugin(SdkContext context, PluginConfiguration<MyPluginConfiguration> configuration) {
        super(context);
        this.config = configuration.getExtendedConfig();
    }

    private Duration timeout() {
        try { return Duration.ofSeconds(Long.parseLong(config.getTimeoutSeconds())); }
        catch (Exception e) { return Duration.ofSeconds(30); }
    }

    /** Response: { "errors": null, "active": true } */
    @Override
    public Response<JsonNode> testConnection(JsonNode request) throws WorkflowExecutionException {
        TestConnectionResponse<JsonNode> response = new TestConnectionResponse<>();
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(config.getBaseUrl() + "/v1/ping"))
                    .timeout(timeout()).header("Authorization", "Bearer " + config.getApiKey()).GET().build();
            int code = HttpClient.newBuilder().connectTimeout(timeout()).build()
                    .send(req, HttpResponse.BodyHandlers.discarding()).statusCode();
            response.setActive(code / 100 == 2);
            response.setMessage("HTTP " + code);
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WorkflowExecutionException("Interrupted in testConnection", e);
        } catch (Exception e) {
            // Unreachable CA is a status, not a crash. Never include the API key in messages.
            response.setActive(false);
            response.setMessage("Cannot reach CA: " + e.getClass().getSimpleName());
            return response;
        }
    }

    /** Response: { "issuers": [ { "id": "issuer-1", "name": "Example Issuing CA 1" } ] } */
    @Override
    public Response<JsonNode> listIssuers(JsonNode request) throws WorkflowExecutionException {
        // TODO: GET {baseUrl}/v1/accounts/{accountId}/issuers
        ListIssuersResponse<JsonNode> response = new ListIssuersResponse<>();
        ListIssuersResponse.Issuer issuer = new ListIssuersResponse.Issuer();
        issuer.setId("issuer-1");
        issuer.setName("Example Issuing CA 1");
        response.setIssuers(List.of(issuer));
        return response;
    }

    @Override
    public Response<JsonNode> listCaProfiles(JsonNode request) throws WorkflowExecutionException {
        try {
            ListCaProfilesRequest<JsonNode> req = PluginUtils.convertWrappedObject(request, ListCaProfilesRequest.class);
            log.info("listCaProfiles flowId={} issuerId={}", req.getFlowId(), req.getIssuerId());
            // TODO: GET {baseUrl}/v1/issuers/{issuerId}/profiles
            ListCaProfilesResponse.CaProfile p = new ListCaProfilesResponse.CaProfile();
            p.setId("tls-server");
            p.setName("TLS Server");
            p.setAllowedKeyAlgorithms(List.of("RSA-2048", "ECDSA-P256"));
            p.setMaxValidityDays(365);
            p.setAllowedSanTypes(List.of("DNS", "IP"));
            ListCaProfilesResponse<JsonNode> response = new ListCaProfilesResponse<>();
            response.setProfiles(List.of(p));
            return response;
        } catch (Exception e) {
            throw new WorkflowExecutionException("Error in listCaProfiles: " + e.getMessage(), e);
        }
    }

    @Override
    public Response<JsonNode> issueCertificate(JsonNode request) throws WorkflowExecutionException {
        try {
            IssueCertificateRequest<JsonNode> req = PluginUtils.convertWrappedObject(request, IssueCertificateRequest.class);
            // Log ids only: the request holds the CSR and profile settings.
            log.info("issueCertificate flowId={} issuer={} caProfile={}", req.getFlowId(), req.getIssuerId(), req.getCaProfileId());
            IssueCertificateResponse<JsonNode> response = new IssueCertificateResponse<>();
            if (req.getCsr() == null || req.getCsr().isBlank()) {
                response.setErrors(List.of(new PluginError("invalid_request", "CSR is required")));
                return response;
            }
            // TODO: POST the CSR to the CA with req.getFlowId() as the idempotency key.
            //  CA accepted, awaiting approval -> PENDING_APPROVAL + caOrderId
            //  CA signed synchronously        -> ISSUED + certificate + chain (check the cert matches the CSR key)
            //  CA refused                     -> REJECTED + message
            //  timeout / 429 / 5xx            -> throw WorkflowExecutionException so TLM can retry
            response.setStatus(IssueCertificateResponse.Status.PENDING_APPROVAL);
            response.setCaOrderId("order-" + req.getFlowId());
            response.setMessage("Placeholder: awaiting CA approval");
            return response;
        } catch (Exception e) {
            throw new WorkflowExecutionException("Error in issueCertificate: " + e.getMessage(), e);
        }
    }

    @Override
    public Response<JsonNode> pollOrder(JsonNode request) throws WorkflowExecutionException {
        try {
            PollOrderRequest<JsonNode> req = PluginUtils.convertWrappedObject(request, PollOrderRequest.class);
            // TODO: GET {baseUrl}/v1/orders/{caOrderId}; on issuance return ISSUED with leaf + chain PEMs.
            IssueCertificateResponse<JsonNode> response = new IssueCertificateResponse<>();
            response.setStatus(IssueCertificateResponse.Status.PENDING_APPROVAL);
            response.setCaOrderId(req.getCaOrderId());
            response.setMessage("Placeholder: still pending");
            return response;
        } catch (Exception e) {
            throw new WorkflowExecutionException("Error in pollOrder: " + e.getMessage(), e);
        }
    }

    @Override
    public Response<JsonNode> revokeCertificate(JsonNode request) throws WorkflowExecutionException {
        try {
            RevokeCertificateRequest<JsonNode> req = PluginUtils.convertWrappedObject(request, RevokeCertificateRequest.class);
            // TODO: POST {baseUrl}/v1/orders/{caOrderId}/revoke with req.getReason()
            RevokeCertificateResponse<JsonNode> response = new RevokeCertificateResponse<>();
            response.setRevoked(true);
            response.setMessage("Placeholder: revoked " + req.getCaOrderId());
            return response;
        } catch (Exception e) {
            throw new WorkflowExecutionException("Error in revokeCertificate: " + e.getMessage(), e);
        }
    }
}
