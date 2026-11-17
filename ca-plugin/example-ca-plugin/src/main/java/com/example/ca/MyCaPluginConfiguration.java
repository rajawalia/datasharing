package com.example.ca;

/**
 * Connector settings. Every property must have a matching field under
 * config_settings -> config_attributes in config/connector-configuration.yaml.
 */
public class MyCaPluginConfiguration {
    public String baseUrl;
    public String accountId;
    public String apiKey;          // marked "sensitive" in credential_sets
    public int timeoutSeconds = 30;
}
