package dk.kvalitetsit.hello.configuration;

import dk.kvalitetsit.hello.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@EnableScheduling
public class MonitoringConfiguration {

    @Autowired
    private MonitoringService monitoringService;
    @Scheduled(fixedRateString = "${FREQUENCY}")
    public void monitorThis() {
        monitoringService.loopingMonitors();
    }


}
