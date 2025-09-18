package org.edderna.springonal.localenv.container;

import org.edderna.springonal.localenv.configuration.InitializableDatabaseContainerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public abstract class InitializedDbContainer<T extends InitializableDatabaseContainerConfig> extends GenericDbContainer<T> {

    public InitializedDbContainer(String imageName, T config) {
        super(imageName, config);
    }

    @Override
    public void start() {
        super.start();

        try {
            customizeAfterStart(config);
            if (!Objects.isNull(config.getInitScripts())) {
                config.getInitScripts().sort(String::compareTo);
                for (String scriptPath : config.getInitScripts()) {
                    runScript(scriptPath);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void runScriptContent(String scriptContent) throws IOException, InterruptedException {
        // You must implement this method in the subclass if you declare a list of initialization scripts
    }

    private void runScript(String scriptPath) throws IOException, InterruptedException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(scriptPath)) {
            String scriptContent = new String(Objects.requireNonNull(is).readAllBytes());
            runScriptContent(scriptContent);
        }
    }


}
