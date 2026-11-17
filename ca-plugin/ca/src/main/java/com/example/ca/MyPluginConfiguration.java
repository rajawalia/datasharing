package com.example.ca;

import lombok.Data;

/**
 * Connector settings entered by the admin in TLM. Every property needs a matching
 * "config_attributes.<name>" entry in configuration.json (config_settings).
 */
@Data
public class MyPluginConfiguration {
    private String baseUrl;
    private String accountId;
    private String apiKey;
    private String timeoutSeconds;
}
