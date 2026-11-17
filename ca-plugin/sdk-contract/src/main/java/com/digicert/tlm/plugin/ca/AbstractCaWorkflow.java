package com.digicert.tlm.plugin.ca;

import com.digicert.tlm.plugin.ca.CaModels.*;

/**
 * Base class for third-party Certificate Authority plugins (proposed 3rd plugin type,
 * alongside AbstractAutomationWorkflow and AbstractDiscoveryWorkflow).
 *
 * @param <C> the plugin's connector configuration class (MyPluginConfiguration);
 *            populated from the connector's config_settings by the Sensor.
 *
 * The Sensor creates one plugin instance per call, injects the configuration, and
 * invokes exactly one method. Implementations must be stateless between calls and
 * idempotent on {@code flowId}: TLM may repeat a call after a Sensor restart.
 */
public abstract class AbstractCaWorkflow<C> {

    protected C config;

    /** Called by the runner before any workflow method. */
    public void init(C config) { this.config = config; }

    // ---------- required ----------

    /** Verify credentials and reachability. Return active=false (don't throw) for auth/network problems. */
    public abstract TestConnectionResponse testConnection();

    /** Issuing CAs / accounts / organizations selectable in a profile. */
    public abstract ListIssuersResponse listIssuers();

    /** CA-side profiles/products for an issuer; populates the profile template dropdowns. */
    public abstract ListCaProfilesResponse listCaProfiles(ListCaProfilesRequest request);

    /**
     * Submit the CSR. Return ISSUED with certificate+chain if the CA signs synchronously,
     * or PENDING_* with a caOrderId, after which TLM calls {@link #pollOrder}.
     */
    public abstract IssueCertificateResponse issueCertificate(IssueCertificateRequest request);

    /** Check an asynchronous order. Only called for orders previously returned as PENDING_*. */
    public abstract IssueCertificateResponse pollOrder(PollOrderRequest request);

    // ---------- optional (declare in supported_operations of the profile template) ----------

    /** Renewal/reissue. Default: a fresh issuance with previousCaOrderId set. */
    public IssueCertificateResponse renewCertificate(IssueCertificateRequest request) {
        return issueCertificate(request);
    }

    /** Revoke. Default: unsupported. List "revoke" in supported_operations only if overridden. */
    public RevokeCertificateResponse revokeCertificate(RevokeCertificateRequest request) {
        throw new CaPluginException("Revocation is not supported by this CA plugin", false);
    }

    /** Import certificates issued out-of-band into TLM inventory. Default: unsupported. */
    public SyncInventoryResponse syncInventory(SyncInventoryRequest request) {
        throw new CaPluginException("Inventory sync is not supported by this CA plugin", false);
    }
}
