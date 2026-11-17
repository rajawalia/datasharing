# CA plugin type: specification (draft)

A third plugin type for the TLM extensibility framework, next to **automation**
(install certs on endpoints) and **discovery** (import certs from external sources).
A **CA plugin** lets a third party add a Certificate Authority integration to TLM
without DigiCert code changes, the way the built-in Sectigo, GlobalSign and Step CA
connectors work.

A CA integration is made of three deliverables, packaged in one ZIP:

| Deliverable | File | Purpose |
|---|---|---|
| Plugin code | `*.jar` + `plugin-meta.json` | Talks to the CA; runs on a TLM Sensor |
| Connector | `connector-configuration.yaml` | Portal form for credentials/URL; defines the connector TLM creates |
| Profile template | `profile-template.yaml` | Base template admins pick when creating certificate profiles |

## Execution model
Same as existing plugins: the Sensor runs the plugin, so the CA need not be reachable
from DigiCert's cloud. One call = one operation. Plugins must be stateless and idempotent
on `flowId`.

## Contract (`AbstractCaWorkflow<C>`)
| Method | Required | Called when |
|---|---|---|
| `testConnection()` | yes | Connector saved / "Test" clicked |
| `listIssuers()` | yes | Creating or editing a profile |
| `listCaProfiles(issuerId)` | yes | Same; fills the CA profile dropdown |
| `issueCertificate(req)` | yes | Enrollment (REST, ACME, EST, UI) |
| `pollOrder(req)` | yes | While an order is `PENDING_*` |
| `renewCertificate(req)` | no | Renewal; defaults to `issueCertificate` |
| `revokeCertificate(req)` | no | Revocation requested in TLM |
| `syncInventory(req)` | no | Scheduled import of out-of-band certs |

Optional operations must be declared in `supported_operations` of both YAML files; TLM
hides UI actions for undeclared ones. Types are in `CaModels.java`.

### Order states
`ISSUED` (cert + chain returned) · `PENDING_APPROVAL` / `PENDING_VALIDATION` (TLM polls) ·
`REJECTED` (CA refused, permanent) · `FAILED` (error).

### Errors
Throw `CaPluginException(message, retryable)`. `retryable=true` for timeouts/429/5xx.
`testConnection` returns `active=false` instead of throwing. Never include secrets in messages.

## Connector configuration
Same sections as existing plugin configs (`metadata`, `core_settings`, `config_settings`,
`additional_settings`, `credential_sets`). Differences: `metadata.plugin_type: certificate_authority`
and a new `supported_operations` list. Each `config_attributes` field must match a property in the plugin's
`...Configuration` class. `sensor_id` is mandatory. Secrets go in the `sensitive` credential set.

## Profile template
- `template`: id, name, description, `connector_type` (= connector `metadata.name`), certificate type.
- `ca_settings`: admin-filled fields. `source: listIssuers|listCaProfiles` makes a dropdown loaded
  from the plugin; `depends_on` chains them; `maps_to` says which `IssueCertificateRequest`
  field receives the value (`profileSettings.<name>` for free-form ones).
- `certificate_options`: key algorithms, validity, subject and SAN rules enforced by TLM
  *before* calling the plugin. A stricter CA profile still wins.
- `enrollment_methods`, `supported_operations`.

## Packaging
`./build.sh` produces `plugin-dist/<name>.zip` and prints the SHA-256, as for the existing samples.

## Security guidance
Never log CSRs' private data, API keys or tokens. Validate TLS to the CA. Treat CA responses as
untrusted: check that the returned certificate matches the CSR public key before returning `ISSUED`.

## Assumptions to confirm with the TLM team
This is a **proposed** contract. docs.digicert.com documents the automation and discovery
plugins but not a CA plugin type or the SDK's exact signatures, so:
1. The YAML keys mirror the documented section names, and the new fields (`plugin_type`,
   `supported_operations`, `source`, `maps_to`, `depends_on`) are proposals.
2. `AbstractCaWorkflow` is shipped here as a dependency-free contract; in the real SDK it would
   live in `plugin-sdk` and use its request/response base classes and Lombok/Jackson conventions.
3. TLM server work is needed: new plugin type, connector registration, profile-template loading,
   order polling, revocation hooks.
