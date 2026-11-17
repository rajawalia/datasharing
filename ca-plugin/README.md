# TLM CA plugin type (proposal + sample)

Lets a third party build a Certificate Authority integration for DigiCert Trust Lifecycle
Manager independently: **plugin code + connector + profile template**.

- `docs/CA_PLUGIN_SPEC.md`: the design and contract
- `sdk-contract/`: `AbstractCaWorkflow`, `CaModels`, `CaPluginException`
- `example-ca-plugin/`: sample plugin, `config/connector-configuration.yaml`, `config/profile-template.yaml`

Build the sample (Java 17+, `zip`):

    cd example-ca-plugin && ./build.sh

Upload the ZIP to TLM, create a connector from the connector configuration (choose the managing
Sensor), then create a certificate profile from the "Example CA TLS Server Certificate" template.

Reference docs: [Build your plugin](https://docs.digicert.com/en/trust-lifecycle-manager/build-your-inventory-and-ecosystem/plugins/build-your-plugin.html),
[Build automation plugins](https://docs.digicert.com/en/trust-lifecycle-manager/build-your-inventory-and-ecosystem/plugins/build-your-plugin/build-automation-plugins.html),
[Build discovery plugins](https://docs.digicert.com/en/trust-lifecycle-manager/build-your-inventory-and-ecosystem/plugins/build-your-plugin/build-discovery-plugins.html),
[Create the plugin configuration](https://docs.digicert.com/en/trust-lifecycle-manager/build-your-inventory-and-ecosystem/plugins/create-the-plugin-configuration.html).
