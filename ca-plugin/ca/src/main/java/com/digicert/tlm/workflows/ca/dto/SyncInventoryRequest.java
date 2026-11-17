package com.digicert.tlm.workflows.ca.dto;

import com.digicert.tlm.plugin.Response;
import com.digicert.tlm.workflows.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/** syncState is the opaque value returned by the previous sync (null on first run). */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SyncInventoryRequest<T> extends BaseRequest<T> {
    private String syncState;
}
