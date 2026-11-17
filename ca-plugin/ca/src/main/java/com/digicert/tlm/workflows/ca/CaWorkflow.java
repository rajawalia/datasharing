package com.digicert.tlm.workflows.ca;

import com.digicert.tlm.plugin.Response;
import com.digicert.tlm.workflows.WorkflowExecutionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Contract for Certificate Authority plugins, the third workflow type next to
 * AutomationWorkflow and DiscoveryWorkflow. Same style: JsonNode in, Response out.
 * <p>
 * REFERENCE ADDITION: this belongs in plugin-sdk (com.digicert.tlm.workflows.ca). It is
 * bundled with the sample so the sample builds against plugin-sdk 1.2 unchanged.
 */
public interface CaWorkflow {
    ObjectMapper objectMapper = new ObjectMapper();

    // required
    Response<JsonNode> testConnection(JsonNode request) throws WorkflowExecutionException;
    Response<JsonNode> listIssuers(JsonNode request) throws WorkflowExecutionException;
    Response<JsonNode> listCaProfiles(JsonNode request) throws WorkflowExecutionException;
    Response<JsonNode> issueCertificate(JsonNode request) throws WorkflowExecutionException;
    Response<JsonNode> pollOrder(JsonNode request) throws WorkflowExecutionException;

    // optional: declare in supported_operations of configuration.json / profile-template.json
    default Response<JsonNode> renewCertificate(JsonNode request) throws WorkflowExecutionException {
        return issueCertificate(request);
    }
    default Response<JsonNode> revokeCertificate(JsonNode request) throws WorkflowExecutionException {
        throw new WorkflowExecutionException("revokeCertificate is not supported by this CA plugin");
    }
    default Response<JsonNode> syncInventory(JsonNode request) throws WorkflowExecutionException {
        throw new WorkflowExecutionException("syncInventory is not supported by this CA plugin");
    }
}
