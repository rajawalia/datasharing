package com.digicert.tlm.workflows.ca.dto;

import com.digicert.tlm.plugin.Response;
import com.digicert.tlm.workflows.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * flowId (from BaseRequest) is the idempotency key: TLM may repeat a call after a Sensor restart.
 * profileSettings carries the values of the profile template's ca_settings fields.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IssueCertificateRequest<T> extends BaseRequest<T> {
    private String csr;                       // PKCS#10, PEM
    private String subjectDn;
    private List<String> dnsNames;
    private List<String> ipAddresses;
    private List<String> emails;
    private List<String> uris;
    private int validityDays;
    private String issuerId;
    private String caProfileId;
    private Map<String, String> profileSettings;
    private String previousCaOrderId;            // set on renew/reissue
}
