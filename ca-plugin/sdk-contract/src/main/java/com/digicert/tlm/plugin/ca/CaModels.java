package com.digicert.tlm.plugin.ca;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Request/response types for the CA plugin contract. Plain records so a CA plugin
 * author needs no extra dependencies; the Sensor serializes them as JSON.
 */
public final class CaModels {
    private CaModels() {}

    /** Result status of an issuance/renewal/poll call. */
    public enum OrderStatus { ISSUED, PENDING_APPROVAL, PENDING_VALIDATION, REJECTED, FAILED }

    /** RFC 5280 revocation reasons TLM can send. */
    public enum RevocationReason {
        UNSPECIFIED, KEY_COMPROMISE, CA_COMPROMISE, AFFILIATION_CHANGED,
        SUPERSEDED, CESSATION_OF_OPERATION, CERTIFICATE_HOLD
    }

    // ---- connector level ----
    public record TestConnectionResponse(boolean active, String message) {}

    /** An issuing CA / account / organization the connector can issue from. */
    public record IssuerInfo(String id, String name, String description) {}
    public record ListIssuersResponse(List<IssuerInfo> issuers) {}

    /** A CA-side profile/product/template. Feeds the dropdowns of the TLM profile template. */
    public record CaProfileInfo(String id, String name, String description,
                                List<String> allowedKeyAlgorithms,
                                Integer maxValidityDays,
                                List<String> allowedSanTypes) {}
    public record ListCaProfilesRequest(String issuerId) {}
    public record ListCaProfilesResponse(List<CaProfileInfo> profiles) {}

    // ---- issuance ----
    /**
     * @param flowId          TLM correlation id; log it, and use it as the idempotency key
     * @param csrPem          PKCS#10 CSR, PEM
     * @param subjectDn       requested subject DN (the CA may override)
     * @param sanDns/sanIps/sanEmails/sanUris requested subject alternative names
     * @param validityDays    requested validity; the CA may cap it
     * @param issuerId        selected IssuerInfo.id
     * @param caProfileId     selected CaProfileInfo.id
     * @param profileSettings values of the profile-template's ca_settings fields, by field name
     * @param previousCaOrderId set when renewing/reissuing, else null
     */
    public record IssueCertificateRequest(String flowId, String csrPem, String subjectDn,
                                          List<String> sanDns, List<String> sanIps,
                                          List<String> sanEmails, List<String> sanUris,
                                          int validityDays, String issuerId, String caProfileId,
                                          Map<String, String> profileSettings,
                                          String previousCaOrderId) {}

    /**
     * @param caOrderId   CA's identifier; TLM stores it and passes it back on poll/revoke
     * @param certificatePem end-entity certificate, only when status == ISSUED
     * @param chainPem    issuer chain, leaf excluded, only when status == ISSUED
     * @param message     human readable detail, surfaced in TLM on PENDING/REJECTED/FAILED
     */
    public record IssueCertificateResponse(OrderStatus status, String caOrderId,
                                           String certificatePem, String chainPem,
                                           String serialNumber, Instant notBefore,
                                           Instant notAfter, String message) {}

    public record PollOrderRequest(String flowId, String caOrderId) {}

    // ---- revocation ----
    public record RevokeCertificateRequest(String flowId, String caOrderId, String serialNumber,
                                           RevocationReason reason) {}
    public record RevokeCertificateResponse(boolean revoked, String message) {}

    // ---- optional inventory sync ----
    /** @param syncState opaque string returned by the previous sync, null on first run */
    public record SyncInventoryRequest(String syncState) {}
    public record CaCertificate(String caOrderId, String certificatePem, String status) {}
    public record SyncInventoryResponse(List<CaCertificate> certificates, String syncState) {}
}
