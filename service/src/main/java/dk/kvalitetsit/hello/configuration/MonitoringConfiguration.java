package dk.kvalitetsit.hello.configuration;

import dk.kvalitetsit.hello.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;


@Configuration
@EnableScheduling
public class MonitoringConfiguration {

    @Autowired
    private MonitoringService monitoringService;
    @Scheduled(fixedRate = 5000)
    public void monitorThis() throws IOException, InterruptedException {
        monitoringService.loopingMonitors();
    }


}
