package com.digicert.tlm.workflows.ca.dto;

import com.digicert.tlm.plugin.Response;
import com.digicert.tlm.workflows.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/** CA-side profiles/products; feeds the dropdowns of the TLM profile template. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListCaProfilesResponse<T> extends Response<T> {
    private List<CaProfile> profiles;

    @Data
    @NoArgsConstructor
    public static class CaProfile {
        private String id;
        private String name;
        private String description;
        private List<String> allowedKeyAlgorithms;   // e.g. RSA-2048, ECDSA-P256
        private Integer maxValidityDays;
        private List<String> allowedSanTypes;        // DNS, IP, EMAIL, URI
    }
}
