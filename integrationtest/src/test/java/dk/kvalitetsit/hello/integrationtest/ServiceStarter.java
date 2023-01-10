package dk.kvalitetsit.hello.integrationtest;


import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.VolumesFrom;
import dk.kvalitetsit.hello.Application;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.function.Consumer;


public class ServiceStarter {
    private static final Logger logger = LoggerFactory.getLogger(ServiceStarter.class);
    private static final Logger serviceLogger = LoggerFactory.getLogger("stakit-adapter-http");
    private static final Logger updateLogger = LoggerFactory.getLogger("update");
    private static final Logger backendLogger = LoggerFactory.getLogger("backend");

    private Network dockerNetwork;
    private int updateServicePort;
    private int backendServicePort;

    private MockServerClient mockServerUpdateClient;
    private MockServerClient mockServerBackendClient;

    public void startServices() {
        //System.out.println(Paths.get(".").toAbsolutePath());
        dockerNetwork = Network.newNetwork();

        setupCimUpdateMockServer();
        setupCimBackendMockServer();

        if (Files.exists(Path.of("integrationtest/src/test/resources/TestConfiguration.yaml"))){
            System.setProperty("Configuration-yaml", "integrationtest/src/test/resources/TestConfiguration.yaml");
        } else {
            System.setProperty("Configuration-yaml", "src/test/resources/TestConfiguration.yaml");
        }



        SpringApplication.run(Application.class);
    }

    public GenericContainer<?> startServicesInDocker() {
        dockerNetwork = Network.newNetwork();

        setupCimUpdateMockServer();
        setupCimBackendMockServer();

        var resourcesContainerName = "stakit-adapter-http-resources";
        var resourcesRunning = containerRunning(resourcesContainerName);
        logger.info("Resource container is running: " + resourcesRunning);

        GenericContainer<?> service;

        // Start service
        if (resourcesRunning) {
            VolumesFrom volumesFrom = new VolumesFrom(resourcesContainerName);
            service = new GenericContainer<>("local/stakit-adapter-http-qa:dev")
                    .withCreateContainerCmdModifier(modifier -> modifier.withVolumesFrom(volumesFrom))
                    .withEnv("JVM_OPTS", "-javaagent:/jacoco/jacocoagent.jar=output=file,destfile=/jacoco-report/jacoco-it.exec,dumponexit=true,append=true -cp integrationtest.jar");
        } else {
            service = new GenericContainer<>("local/stakit-adapter-http-qa:dev")
                    .withFileSystemBind("/tmp", "/jacoco-report/")
                    .withEnv("JVM_OPTS", "-javaagent:/jacoco/jacocoagent.jar=output=file,destfile=/jacoco-report/jacoco-it.exec,dumponexit=true -cp integrationtest.jar");
        }

        service.withNetwork(dockerNetwork)
                .withNetworkAliases("stakit-adapter-http")

                .withEnv("LOG_LEVEL", "INFO")

                .withEnv("Configuration-yaml", "/TestConfiguration-docker.yaml")

                .withClasspathResourceMapping("TestConfiguration-docker.yaml", "/TestConfiguration-docker.yaml", BindMode.READ_ONLY)

                .withExposedPorts(8081,8080)
                .waitingFor(Wait.forHttp("/actuator").forPort(8081).forStatusCode(200));
        service.start();
        attachLogger(serviceLogger, service);

        return service;
    }

    private boolean containerRunning(String containerName) {
        return DockerClientFactory
                .instance()
                .client()
                .listContainersCmd()
                .withNameFilter(Collections.singleton(containerName))
                .exec()
                .size() != 0;
    }

    private void attachLogger(Logger logger, GenericContainer<?> container) {
        ServiceStarter.logger.info("Attaching logger to container: " + container.getContainerInfo().getName());
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        container.followOutput(logConsumer);
    }


    //Doing mockerStuff
    private void setupCimUpdateMockServer(){
        int phpMyAdminPort = 1080;
        int phpMyAdminContainerPort = 1080;
        Consumer<CreateContainerCmd> cmd = e -> e.withPortBindings(new PortBinding(Ports.Binding.bindPort(phpMyAdminPort), new ExposedPort(phpMyAdminContainerPort)));
        MockServerContainer updateservice = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.14.0"))
                .withNetwork(dockerNetwork)
                .withNetworkAliases("updateservice")
                .withCreateContainerCmdModifier(cmd);
        updateservice.start();
        mockServerUpdateClient = new MockServerClient(updateservice.getContainerIpAddress(), updateservice.getMappedPort(1080));
        mockServerUpdateClient.when(HttpRequest.request().withPath("/is/ok").withMethod("GET").withHeader("X-API-KEY", "test-key"), Times.unlimited()).respond(HttpResponse.response().withStatusCode(200));
        mockServerUpdateClient.when(HttpRequest.request().withPath("/is/not/ok").withMethod("GET").withHeader("X-API-KEY", "test-key"), Times.unlimited()).respond(HttpResponse.response().withStatusCode(500));
        updateServicePort = updateservice.getMappedPort(1080);

        attachLogger(updateLogger, updateservice);
    }

    public MockServerClient getMockServerUpdateClient(){
        return mockServerUpdateClient;
    }

    private void setupCimBackendMockServer(){
        int phpMyAdminPort = 1090;
        int phpMyAdminContainerPort = 1080;
        Consumer<CreateContainerCmd> cmd = e -> e.withPortBindings(new PortBinding(Ports.Binding.bindPort(phpMyAdminPort), new ExposedPort(phpMyAdminContainerPort)));
        MockServerContainer backendservice = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.14.0"))
                .withNetwork(dockerNetwork)
                .withNetworkAliases("backendservice")
                .withCreateContainerCmdModifier(cmd);
        backendservice.start();
        mockServerBackendClient = new MockServerClient(backendservice.getContainerIpAddress(), backendservice.getMappedPort(1080));
        mockServerBackendClient.when(HttpRequest.request().withPath("/v1/status").withMethod("POST").withHeader("X-API-KEY", "MY-SUPE-SECRET-KEY"), Times.unlimited()).respond(HttpResponse.response().withStatusCode(201));
        backendServicePort = backendservice.getMappedPort(1080);

        attachLogger(backendLogger, backendservice);
    }

    public MockServerClient getMockServerBackendClient(){
        return mockServerBackendClient;
    }
}
