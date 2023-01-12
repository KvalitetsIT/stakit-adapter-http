package dk.kvalitetsit.hello.integrationtest;

import org.mockserver.client.MockServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.net.URISyntaxException;

public abstract class AbstractIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    private static GenericContainer updateService;
    private static String apiBasePath;

    private static ServiceStarter serviceStarter;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                if(updateService != null) {
                    logger.info("Stopping hello service container: " + updateService.getContainerId());
                    updateService.getDockerClient().stopContainerCmd(updateService.getContainerId()).exec();
                }
            }
        });

        try {
            setup();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setup() throws IOException, URISyntaxException {
        var runInDocker = Boolean.getBoolean("runInDocker");
        logger.info("Running integration test in docker container: " + runInDocker);

        serviceStarter = new ServiceStarter();
        if(runInDocker) {
            updateService = serviceStarter.startServicesInDocker();
            apiBasePath = "http://" + updateService.getContainerIpAddress() + ":" + updateService.getMappedPort(8080);
        }
        else {
            serviceStarter.startServices();
            apiBasePath = "http://localhost:8080";
        }
    }

    protected MockServerClient getMockServerUpdateClient() {
        return serviceStarter.getMockServerUpdateClient();
    }

    protected MockServerClient getMockServerBackendClient() {
        return serviceStarter.getMockServerBackendClient();
    }
}
