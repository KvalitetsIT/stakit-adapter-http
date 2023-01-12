package dk.kvalitetsit.hello.service;

import dk.kvalitetsit.hello.service.model.ConfigurationModel;
import dk.kvalitetsit.hello.service.model.Monitoring;
import dk.kvalitetsit.hello.stakitClient.StatusCode;
import dk.kvalitetsit.hello.stakitClient.model.StatusUpdate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

public class MonitoringServiceTest {

    //For mocking
    private HttpMonitorService httpMonitorService;
    private StatusCode statusCode;
    private Monitoring monitoring0;
    private Monitoring monitoring1;

    private MonitoringService monitoringService;


    @Before
    public void setup() {
        httpMonitorService = Mockito.mock(HttpMonitorService.class);
        statusCode = Mockito.mock(StatusCode.class);

        ConfigurationModel configurationModel = new ConfigurationModel();
        monitoring0 = new Monitoring();
        monitoring1 = new Monitoring();
        List<Monitoring> monitoringList = new ArrayList<>();

        monitoring0.setEndpoint("url0");
        monitoring1.setEndpoint("url1");
        monitoring0.setServiceName("name0");
        monitoring1.setServiceName("name1");
        monitoring0.setServiceIdentifier("id0");
        monitoring1.setServiceIdentifier("id1");
        monitoringList.add(monitoring0);
        monitoringList.add(monitoring1);
        configurationModel.setMonitoring(monitoringList);

        monitoringService = new MonitoringService(configurationModel, httpMonitorService, statusCode);
    }

    @Test
    public void testLoopingMonitors() {
        Mockito.when(httpMonitorService.getResult(Mockito.any())).thenReturn(true);

        monitoringService.loopingMonitors();

        var inputArgumentCaptorHttp = ArgumentCaptor.forClass(String.class);
        Mockito.verify(httpMonitorService, times(2)).getResult(inputArgumentCaptorHttp.capture());

        assertNotNull(inputArgumentCaptorHttp.getAllValues());
        assertEquals(monitoring0.getEndpoint(), inputArgumentCaptorHttp.getAllValues().get(0));
        assertEquals(monitoring1.getEndpoint(), inputArgumentCaptorHttp.getAllValues().get(1));

        var inputArgumentCaptorStatus = ArgumentCaptor.forClass(StatusUpdate.class);
        Mockito.verify(statusCode, times(2)).update(inputArgumentCaptorStatus.capture());
        assertEquals("name0", inputArgumentCaptorStatus.getAllValues().get(0).getServiceName());
        assertEquals("name1", inputArgumentCaptorStatus.getAllValues().get(1).getServiceName());
        assertEquals("id0", inputArgumentCaptorStatus.getAllValues().get(0).getService());
        assertEquals("id1", inputArgumentCaptorStatus.getAllValues().get(1).getService());
        assertEquals(StatusUpdate.Status.OK, inputArgumentCaptorStatus.getAllValues().get(0).getStatus());
        assertEquals(StatusUpdate.Status.OK, inputArgumentCaptorStatus.getAllValues().get(1).getStatus());
    }
}
