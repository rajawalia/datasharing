package com.digicert.tlm.workflows.ca.dto;

import com.digicert.tlm.plugin.Response;
import com.digicert.tlm.workflows.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/** Issuing CAs / accounts / organizations the connector can issue from. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListIssuersResponse<T> extends Response<T> {
    private List<Issuer> issuers;

    @Data
    @NoArgsConstructor
    public static class Issuer {
        private String id;
        private String name;
        private String description;
    }
}
