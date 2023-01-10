package dk.kvalitetsit.hello.service;

import dk.kvalitetsit.hello.service.model.ConfigurationModel;
import dk.kvalitetsit.hello.service.model.Monitoring;
import dk.kvalitetsit.hello.stakitClient.StatusCode;
import dk.kvalitetsit.hello.stakitClient.model.StatusUpdate;

import java.io.IOException;

import java.util.List;

public class MonitoringService {

    private final ConfigurationModel configurationModel;
    private final HttpMonitorService httpMonitorService;
    private final StatusCode statusCode;

    public MonitoringService(ConfigurationModel input, HttpMonitorService httpMonitorService, StatusCode statusCode) {
        this.configurationModel = input;
        this.httpMonitorService = httpMonitorService;
        this.statusCode = statusCode;
    }

    public void loopingMonitors() throws IOException, InterruptedException {
        List<Monitoring> monitoringList = this.configurationModel.getMonitoring();
        for (Monitoring monitoring: monitoringList){
            String serviceName = monitoring.getServiceName();
            String service = monitoring.getServiceIdentifier();
            String url = monitoring.getEndpoint();

            boolean result = this.httpMonitorService.getResult(url);
            System.out.println(url + ": " + result);

            StatusUpdate statusUpdate = new StatusUpdate();
            statusUpdate.setServiceName(serviceName);
            statusUpdate.setService(service);
            statusUpdate.setStatus(result);
            this.statusCode.update(statusUpdate);
        }
    }
}
