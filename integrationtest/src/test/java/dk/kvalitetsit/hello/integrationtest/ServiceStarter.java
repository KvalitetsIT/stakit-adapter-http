package dk.kvalitetsit.hello.integrationtest;

import com.github.dockerjava.api.model.VolumesFrom;
import dk.kvalitetsit.hello.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Collections;

public class ServiceStarter {
    private static final Logger logger = LoggerFactory.getLogger(ServiceStarter.class);
    private static final Logger serviceLogger = LoggerFactory.getLogger("stakit-adapter-http");

    private Network dockerNetwork;
    private String jdbcUrl;

    public void startServices() {
        dockerNetwork = Network.newNetwork();

        SpringApplication.run(Application.class);
    }

    public GenericContainer<?> startServicesInDocker() {
        dockerNetwork = Network.newNetwork();

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

                .withEnv("JDBC_URL", "jdbc:mariadb://mariadb:3306/hellodb")
                .withEnv("JDBC_USER", "hellouser")
                .withEnv("JDBC_PASS", "secret1234")

                .withEnv("spring.flyway.locations", "classpath:db/migration,filesystem:/app/sql")
                .withClasspathResourceMapping("db/migration/V901__extra_data_for_integration_test.sql", "/app/sql/V901__extra_data_for_integration_test.sql", BindMode.READ_ONLY)
//                .withEnv("JVM_OPTS", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000")

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
}
