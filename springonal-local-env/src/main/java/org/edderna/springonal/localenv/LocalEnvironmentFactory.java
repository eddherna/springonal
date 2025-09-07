package org.edderna.springonal.localenv;

import com.moandjiezana.toml.Toml;
import org.edderna.springonal.localenv.configuration.LocalEnvironment;

import java.io.IOException;
import java.io.InputStream;

public class LocalEnvironmentFactory {


    public LocalEnvironment create(String filePath) throws IOException {

        try (InputStream inputStream = getClass().getResourceAsStream(filePath)) {
            Toml toml = new Toml().read(inputStream);
            return new LocalEnvironment(toml);
        }

    }

}
