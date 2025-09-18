package org.edderna.springonal.localenv.configuration;

import com.moandjiezana.toml.Toml;

import java.util.ArrayList;
import java.util.List;

public class InitializableDatabaseContainerConfig extends AuthenticableDatabaseContainerConfig {

    protected List<String> initScripts;

    public InitializableDatabaseContainerConfig(Toml toml) {
        super(toml);
        initScripts = toml.getList("init-scripts", new ArrayList<>());
    }

    public List<String> getInitScripts() {
        return initScripts;
    }
}
