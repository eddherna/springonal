package org.edderna.springonal.localenv.container;

import org.edderna.springonal.localenv.configuration.wiremock.WiremockContainerConfig;
import org.testcontainers.containers.BindMode;
import org.testcontainers.images.builder.ImageFromDockerfile;

public class WiremockContainer extends GenericSpringonalContainer<WiremockContainerConfig> {
    public WiremockContainer(WiremockContainerConfig config) {
        super(new ImageFromDockerfile()
                .withDockerfileFromBuilder(b -> {
                    b.from("wiremock/wiremock:" + config.getVersion());
                    if (config.hasResponseTemplating()) {
                        b.entryPoint("/docker-entrypoint.sh", "--global-response-templating");
                    }
                }), config);
    }

    @Override
    protected void customizeContainerBeforeStart() {
        withExposedPorts(8080);
        withClasspathResourceMapping(config.getMappings(), "/home/wiremock/mappings", BindMode.READ_ONLY);
        withClasspathResourceMapping(config.getFiles(), "/home/wiremock/__files", BindMode.READ_ONLY);
    }
}
