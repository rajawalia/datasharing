# TLM CA plugin example

Sample **certificate authority** plugin, laid out like `automation/` and `discovery/` in
[tlm-plugin-examples](https://github.com/digicert/tlm-plugin-examples). A CA integration has three parts:

| Part | File | Uploaded to TLM as |
|---|---|---|
| Plugin | `plugin-dist/*.zip` (jar + `plugin-meta.json`) | the plugin package |
| Connector | `configuration.json` | the plugin configuration (credentials, managing sensor) |
| Profile template | `profile-template.json` | the base template for certificate profiles |

Build (Java 17+, Maven): `./build.sh`. It resolves `plugin-sdk:1.2` from the public SDK Maven repo.

## Where to implement
`src/main/java/com/example/ca/MyCaPlugin.java`: replace each `TODO` with calls to your CA.
Connector fields live in `MyPluginConfiguration` and must match `config_settings` in `configuration.json`.

## SDK addition
`src/main/java/com/digicert/tlm/workflows/ca/` (`CaWorkflow`, `AbstractCaWorkflow`, `dto/`) is what
plugin-sdk would ship for this plugin type. It is bundled here so the sample builds on SDK 1.2.
`SdkRuntime` in 1.2 hard-codes automation/discovery/cert-delivery dispatch, so `MyCaPluginRunner` calls
`execute()` directly instead.
