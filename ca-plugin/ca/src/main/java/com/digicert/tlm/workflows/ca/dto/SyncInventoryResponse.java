package com.digicert.tlm.workflows.ca.dto;

import com.digicert.tlm.plugin.Response;
import com.digicert.tlm.workflows.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SyncInventoryResponse<T> extends Response<T> {
    private List<CaCertificate> certificates;
    private String syncState;

    @Data
    @NoArgsConstructor
    public static class CaCertificate {
        private String caOrderId;
        private String certificate;              // PEM
        private String status;                   // ACTIVE, REVOKED, EXPIRED
    }
}
