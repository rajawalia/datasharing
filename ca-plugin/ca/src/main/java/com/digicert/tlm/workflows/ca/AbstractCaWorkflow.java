package com.digicert.tlm.workflows.ca;

import com.digicert.tlm.SdkContext;
import com.digicert.tlm.plugin.PluginAction;
import com.digicert.tlm.plugin.PluginError;
import com.digicert.tlm.plugin.PluginUtils;
import com.digicert.tlm.plugin.Response;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Dispatches the action the Sensor requested (see PluginUtils.getPluginRunData) to the
 * matching CaWorkflow method and uploads the Response, mirroring AbstractAutomationWorkflow.
 * Script pre/post hooks are not wired in this reference addition.
 */
public abstract class AbstractCaWorkflow implements CaWorkflow {
    private static final Logger log = LoggerFactory.getLogger(AbstractCaWorkflow.class);

    public enum Actions {
        TEST_CONNECTION("testConnection"), LIST_ISSUERS("listIssuers"), LIST_CA_PROFILES("listCaProfiles"),
        ISSUE_CERTIFICATE("issueCertificate"), POLL_ORDER("pollOrder"), RENEW_CERTIFICATE("renewCertificate"),
        REVOKE_CERTIFICATE("revokeCertificate"), SYNC_INVENTORY("syncInventory");

        private final String action;
        Actions(String action) { this.action = action; }
        public String getAction() { return action; }

        public static Actions fromString(String s) throws NoSuchMethodException {
            for (Actions a : values()) if (a.action.equals(s)) return a;
            throw new NoSuchMethodException("Unknown CA action: " + s);
        }
    }

    private final SdkContext sdkContext;

    protected AbstractCaWorkflow() { this(new SdkContext()); }
    protected AbstractCaWorkflow(SdkContext sdkContext) { this.sdkContext = sdkContext; }
    public SdkContext getSdkContext() { return sdkContext; }

    /** @return true if the action completed without errors */
    public boolean execute() throws Exception {
        String actionName = "unknown";
        try {
            PluginAction run = PluginUtils.getPluginRunData();
            Actions action = Actions.fromString(run.getAction());
            actionName = action.getAction();
            log.debug("Executing CA action: {}", actionName);
            Response<JsonNode> response = executeCaActions(action, run.getParameters());
            boolean ok = response != null && (response.getErrors() == null || response.getErrors().isEmpty());
            PluginUtils.uploadResponse(actionName, response, ok, true);
            return ok;
        } catch (Exception e) {
            // Never echo request bodies here: they contain CSRs and profile settings.
            log.error("Error executing CA action {}: {}", actionName, e.getMessage(), e);
            PluginUtils.uploadResponse(actionName,
                new Response<>(List.of(new PluginError("failed", e.getMessage()))), false, true);
            return false;
        }
    }

    public Response<JsonNode> executeCaActions(Actions action, JsonNode params) throws Exception {
        return switch (action) {
            case TEST_CONNECTION -> testConnection(params);
            case LIST_ISSUERS -> listIssuers(params);
            case LIST_CA_PROFILES -> listCaProfiles(params);
            case ISSUE_CERTIFICATE -> issueCertificate(params);
            case POLL_ORDER -> pollOrder(params);
            case RENEW_CERTIFICATE -> renewCertificate(params);
            case REVOKE_CERTIFICATE -> revokeCertificate(params);
            case SYNC_INVENTORY -> syncInventory(params);
        };
    }
}
