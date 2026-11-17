package com.digicert.tlm.workflows.ca.dto;

import com.digicert.tlm.plugin.Response;
import com.digicert.tlm.workflows.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/** status ISSUED carries certificate + chain; PENDING_* carries only caOrderId. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IssueCertificateResponse<T> extends Response<T> {
    public enum Status { ISSUED, PENDING_APPROVAL, PENDING_VALIDATION, REJECTED, FAILED }

    private Status status;
    private String caOrderId;                    // stored by TLM, passed back on poll/revoke
    private String certificate;                  // end-entity, PEM
    private String chain;                        // issuer chain without the leaf, PEM
    private String serialNumber;
    private String notBefore;                    // ISO-8601
    private String notAfter;                     // ISO-8601
    private String message;                      // shown in TLM for PENDING/REJECTED/FAILED
}
