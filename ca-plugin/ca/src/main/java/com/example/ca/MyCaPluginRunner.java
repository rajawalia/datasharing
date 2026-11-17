package com.example.ca;

import com.digicert.tlm.SdkContext;
import com.digicert.tlm.plugin.PluginUtils;
import com.digicert.tlm.plugin.model.PluginConfiguration;

/**
 * Entry point launched by the Sensor (see plugin-meta.json). Do not modify; its fully
 * qualified name must match the mainClass in pom.xml.
 * <p>
 * SdkRuntime in plugin-sdk 1.2 only dispatches automation/discovery/cert-delivery plugins,
 * so this runner builds the plugin itself. Once CA support is in the SDK, replace the body
 * with: new SdkRuntime(pkg).execute("MyCaPlugin", new SdkContext());
 */
public class MyCaPluginRunner {
    public static void main(String[] args) throws Exception {
        PluginConfiguration<MyPluginConfiguration> configuration =
                PluginUtils.getPluginConfiguration(MyPluginConfiguration.class);
        boolean ok = new MyCaPlugin(new SdkContext(), configuration).execute();
        System.exit(ok ? 0 : 1);
    }
}
