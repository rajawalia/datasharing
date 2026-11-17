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
public class RevokeCertificateResponse<T> extends Response<T> {
    private boolean revoked;
    private String message;
}
