package com.example.ca;

/**
 * Entry point launched by the Sensor (named in plugin-meta.json / pom.xml).
 * Do not modify; its fully qualified name must match the pom.xml Main-Class.
 * The real SDK runner reads the operation + JSON input from the Sensor context;
 * this sample only exercises the plugin locally.
 */
public class MyCaPluginRunner {
    public static void main(String[] args) {
        MyCaPluginConfiguration cfg = new MyCaPluginConfiguration();
        cfg.baseUrl = args.length > 0 ? args[0] : "https://ca.example.invalid";
        cfg.apiKey = args.length > 1 ? args[1] : "";
        MyCaPlugin plugin = new MyCaPlugin();
        plugin.init(cfg);
        System.out.println(plugin.testConnection());
    }
}
