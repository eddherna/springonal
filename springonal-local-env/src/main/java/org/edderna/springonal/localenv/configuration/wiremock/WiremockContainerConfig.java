package org.edderna.springonal.localenv.configuration.wiremock;

import com.moandjiezana.toml.Toml;
import org.edderna.springonal.localenv.configuration.AbstractContainerConfig;

public class WiremockContainerConfig extends AbstractContainerConfig {

    private String mappings;
    private String files;
    private boolean responseTemplating;

    public WiremockContainerConfig(Toml toml) {
        super(toml);
        mappings = toml.getString("mappings");
        files = toml.getString("files");
        responseTemplating = toml.getBoolean("responseTemplating", false);
    }

    public String getMappings() {
        return mappings;
    }

    public String getFiles() {
        return files;
    }

    public boolean hasResponseTemplating() {
        return responseTemplating;
    }
}
