package dk.kvalitetsit.hello.service;

import dk.kvalitetsit.hello.service.model.Monitoring;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MonitoringServiceTest {

    private MonitoringService monitoringService;


    @Before
    public void setup() {
        List<Monitoring> monitoringList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Monitoring monitoring = new Monitoring();
            monitoring.setEndpoint("endpoint" + Integer.toString(i));
            monitoring.setServiceName("serviceName" + Integer.toString(i));
            monitoring.setServiceIdentifier("serviceIdentifier" + Integer.toString(i));
            monitoringList.add(monitoring);

        }
        monitoringService = new MonitoringService(monitoringList);
    }

    @Test
    public void testPrinting() {
        monitoringService.loopingMonitors();
    }
}
