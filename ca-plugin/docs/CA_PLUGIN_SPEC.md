# CA plugin type: specification (draft)

A third plugin type for the TLM extensibility framework, next to **automation**
(install certs on endpoints) and **discovery** (import certs from external sources).
A **CA plugin** lets a third party add a Certificate Authority integration to TLM
without DigiCert code changes, the way the built-in Sectigo, GlobalSign and Step CA
connectors work.

A CA integration is made of three deliverables, packaged in one ZIP:

| Deliverable | File | Purpose |
|---|---|---|
| Plugin code | `*.zip` (jar + `plugin-meta.json`) | Talks to the CA; runs on a TLM Sensor |
| Connector | `configuration.json` | Portal form for credentials/URL; defines the connector TLM creates |
| Profile template | `profile-template.json` | Base template admins pick when creating certificate profiles |

## Execution model
Same as existing plugins: the Sensor runs the plugin, so the CA need not be reachable
from DigiCert's cloud. One call = one operation. Plugins must be stateless and idempotent
on `flowId`.

## Contract (`CaWorkflow` / `AbstractCaWorkflow`)
Same style as `AutomationWorkflow`/`DiscoveryWorkflow` in plugin-sdk 1.2: each method takes a `JsonNode`
and returns `Response<JsonNode>`; the plugin is annotated `@WorkflowEntryPoint`, takes
`(SdkContext, PluginConfiguration<MyPluginConfiguration>)`, and `AbstractCaWorkflow.execute()` reads the
requested action (`PluginUtils.getPluginRunData()`), dispatches it, and uploads the response. DTOs extend
`BaseRequest<T>` / `Response<T>` (flowId, errors, extended request/response).

| Action | Required | Called when |
|---|---|---|
| `testConnection` | yes | Connector saved / "Test" clicked |
| `listIssuers` | yes | Creating or editing a profile |
| `listCaProfiles` (issuerId) | yes | Same; fills the CA profile dropdown |
| `issueCertificate` | yes | Enrollment (REST, ACME, EST, UI) |
| `pollOrder` | yes | While an order is `PENDING_*` |
| `renewCertificate` | no | Renewal; defaults to `issueCertificate` |
| `revokeCertificate` | no | Revocation requested in TLM |
| `syncInventory` | no | Scheduled import of out-of-band certs |

Optional operations are declared in `supported_operations`; TLM hides actions a CA does not support.

### Order states
`ISSUED` (certificate + chain returned) | `PENDING_APPROVAL` / `PENDING_VALIDATION` (TLM polls) |
`REJECTED` (CA refused) | `FAILED`.

### Errors
Throw `WorkflowExecutionException` for failures TLM may retry (timeouts, 429/5xx), or return `errors`
(`PluginError`) for permanent ones. `testConnection` returns `active=false` instead of throwing.
`flowId` is the idempotency key. Never log CSRs, profile settings or credentials.

## Connector (`configuration.json`)
Exactly the existing format: `metadata`, `core_settings` (mandatory managing sensor), `config_settings`
(`config_attributes.*` matching the plugin's configuration class), `additional_settings`, `credential_sets`
(`sensitive`, `unique`). New: a `ca_settings` block with `plugin_type: certificate_authority`, the
`connector_name` profile templates bind to, and `supported_operations`.

## Profile template (`profile-template.json`)
- `template`: id, name, description, `connector_type` (= `connector_name`), certificate type.
- `ca_settings`: admin-filled fields. `options_provider_action` populates a dropdown by calling that plugin
  action (like the existing `options_provider` values such as `Sensors`); `depends_on` chains dropdowns;
  `maps_to` names the `IssueCertificateRequest` field that receives the value (`profileSettings.<name>` for
  free-form fields).
- `certificate_options`: key algorithms, validity, subject/SAN rules TLM enforces before calling the plugin;
  a stricter CA profile wins.
- `enrollment_methods`, `supported_operations`.

## Packaging
`./build.sh` -> `plugin-dist/<artifact>-<version>.zip`, plus the two JSON files uploaded alongside it.

## Security guidance
Validate TLS to the CA. Treat CA responses as untrusted: confirm the returned certificate matches the CSR
public key before returning `ISSUED`. Keep secrets in the `sensitive` credential set.

## Open items for the TLM/SDK team
Verified against plugin-sdk 1.2 and the tlm-plugin-examples repo. docs.digicert.com does not describe a CA
plugin type, so the following are **proposals**:
1. `CaWorkflow`/`AbstractCaWorkflow`/DTOs belong in plugin-sdk, and `SdkRuntime` needs a CA dispatch branch
   (today it only handles automation/discovery/cert-delivery instances). Script pre/post hooks are not wired.
2. TLM server: new plugin type, connector registration, profile-template loading, order polling, revocation.
3. The `ca_settings` blocks and `options_provider_action` are new keys; the rest follows existing formats.
