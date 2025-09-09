package org.edderna.springonal.localenv.configuration.redis;

import com.moandjiezana.toml.Toml;
import org.edderna.springonal.localenv.configuration.AbstractContainerConfig;

public class RedisContainerConfig extends AbstractContainerConfig {
    public RedisContainerConfig(Toml toml) {
        super(toml);
    }
}
