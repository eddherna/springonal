package org.edderna.springonal.localenv.containers;

import com.moandjiezana.toml.Toml;
import org.edderna.springonal.localenv.configuration.wiremock.WiremockContainerConfig;
import org.edderna.springonal.localenv.container.WiremockContainer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class WiremockContainerTest {

    @ParameterizedTest
    @ValueSource(strings = {"3.13.1", "3.10.0", "3.9.0"})
    void testVersion(String version) throws IOException, InterruptedException {
        String toml = """
                version="%s"
                responseTemplating=true
                mappings="wiremock-testcases/complete-wiremock/mappings"
                files="wiremock-testcases/complete-wiremock/files"
                """;
        WiremockContainerConfig config = new WiremockContainerConfig(new Toml().read(String.format(toml, version)));

        try (WiremockContainer container = new WiremockContainer(config)) {
            container.start();

            String baseUrl = "http://localhost:" + container.getMappedPort(8080);


            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            var adminRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/__admin/mappings"))
                    .GET()
                    .build();

            HttpResponse<String> adminResponse = client.send(adminRequest, HttpResponse.BodyHandlers.ofString());

            assertThat(adminResponse.statusCode()).isEqualTo(200);
            assertThat(adminResponse.body()).isEqualTo(
                    """
                            {
                              "mappings" : [ {
                                "id" : "57c83658-c634-4b01-aabf-31b99b804710",
                                "request" : {
                                  "method" : "ANY"
                                },
                                "response" : {
                                  "status" : 200
                                },
                                "uuid" : "57c83658-c634-4b01-aabf-31b99b804710"
                              } ],
                              "meta" : {
                                "total" : 1
                              }
                            }
                            """
            );
            System.out.println();

        }


    }
}
