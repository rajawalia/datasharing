package com.example.ca;

import com.digicert.tlm.plugin.ca.AbstractCaWorkflow;
import com.digicert.tlm.plugin.ca.CaModels.*;
import com.digicert.tlm.plugin.ca.CaPluginException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Sample CA plugin for a hypothetical REST CA ("Example CA").
 * testConnection() makes a real HTTP call. The other methods return clearly
 * marked placeholder data; replace each TODO with your CA's API calls.
 */
public class MyCaPlugin extends AbstractCaWorkflow<MyCaPluginConfiguration> {

    private HttpClient http() {
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(config.timeoutSeconds)).build();
    }

    @Override
    public TestConnectionResponse testConnection() {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(config.baseUrl + "/v1/ping"))
                    .timeout(Duration.ofSeconds(config.timeoutSeconds))
                    .header("Authorization", "Bearer " + config.apiKey)
                    .GET().build();
            int code = http().send(req, HttpResponse.BodyHandlers.discarding()).statusCode();
            if (code == 401 || code == 403) return new TestConnectionResponse(false, "Authentication failed (HTTP " + code + ")");
            return new TestConnectionResponse(code / 100 == 2, "HTTP " + code);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CaPluginException("Interrupted", true, e);
        } catch (Exception e) {
            return new TestConnectionResponse(false, "Cannot reach CA: " + e.getClass().getSimpleName());
        }
    }

    @Override
    public ListIssuersResponse listIssuers() {
        // TODO: GET {baseUrl}/v1/accounts/{accountId}/issuers
        return new ListIssuersResponse(List.of(new IssuerInfo("issuer-1", "Example Issuing CA 1", "Placeholder")));
    }

    @Override
    public ListCaProfilesResponse listCaProfiles(ListCaProfilesRequest request) {
        // TODO: GET {baseUrl}/v1/issuers/{issuerId}/profiles
        return new ListCaProfilesResponse(List.of(new CaProfileInfo(
                "tls-server", "TLS Server", "Placeholder", List.of("RSA-2048", "ECDSA-P256"), 365,
                List.of("DNS", "IP"))));
    }

    @Override
    public IssueCertificateResponse issueCertificate(IssueCertificateRequest request) {
        if (request.csrPem() == null || request.csrPem().isBlank())
            throw new CaPluginException("CSR is required", false);
        // TODO: POST the CSR, using request.flowId() as the idempotency key.
        // Map CA responses: 202/pending -> PENDING_APPROVAL, 4xx -> REJECTED or CaPluginException(false),
        // 429/5xx/timeouts -> CaPluginException(retryable = true).
        Instant now = Instant.now();
        return new IssueCertificateResponse(OrderStatus.PENDING_APPROVAL, "order-" + request.flowId(),
                null, null, null, now, now.plus(request.validityDays(), ChronoUnit.DAYS),
                "Placeholder: awaiting CA approval");
    }

    @Override
    public IssueCertificateResponse pollOrder(PollOrderRequest request) {
        // TODO: GET {baseUrl}/v1/orders/{caOrderId}; on issuance return ISSUED with leaf + chain PEMs.
        return new IssueCertificateResponse(OrderStatus.PENDING_APPROVAL, request.caOrderId(),
                null, null, null, null, null, "Placeholder: still pending");
    }

    @Override
    public RevokeCertificateResponse revokeCertificate(RevokeCertificateRequest request) {
        // TODO: POST {baseUrl}/v1/orders/{caOrderId}/revoke
        return new RevokeCertificateResponse(true, "Placeholder: revoked");
    }
}
