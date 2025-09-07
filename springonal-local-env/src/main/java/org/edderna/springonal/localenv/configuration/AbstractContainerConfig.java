package org.edderna.springonal.localenv.configuration;

import com.moandjiezana.toml.Toml;
import org.edderna.springonal.localenv.exception.MalformedEnviromentException;

public class AbstractContainerConfig {

    private String version;

    public AbstractContainerConfig(Toml toml) {
        this.version = toml.getString("version");

        if (this.version == null) {
            throw new MalformedEnviromentException("Version definition cannot be null.");
        }
    }

    public String getVersion() {
        return version;
    }
}
