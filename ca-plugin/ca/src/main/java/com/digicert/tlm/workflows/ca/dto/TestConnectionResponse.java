package com.digicert.tlm.workflows.ca.dto;

import com.digicert.tlm.plugin.Response;
import com.digicert.tlm.workflows.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/** active=false (not an exception) for bad credentials or an unreachable CA. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TestConnectionResponse<T> extends Response<T> {
    private boolean active;
    private String message;
}
