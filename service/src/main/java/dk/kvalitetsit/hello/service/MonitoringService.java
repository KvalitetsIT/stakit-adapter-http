package dk.kvalitetsit.hello.service;

import dk.kvalitetsit.hello.service.model.ConfigurationModel;
import dk.kvalitetsit.hello.service.model.Monitoring;
import dk.kvalitetsit.hello.stakitClient.StatusCode;
import dk.kvalitetsit.hello.stakitClient.model.StatusUpdate;

import java.time.OffsetDateTime;
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

    public void loopingMonitors() {
        List<Monitoring> monitoringList = this.configurationModel.getMonitoring();
        for (Monitoring monitoring: monitoringList){
            String serviceName = monitoring.getServiceName();
            String service = monitoring.getServiceIdentifier();
            String url = monitoring.getEndpoint();
            StatusUpdate.Status status;

            boolean result = this.httpMonitorService.getResult(url);
            if (result){
                status = StatusUpdate.Status.OK;
            } else {
                status = StatusUpdate.Status.NOT_OK;
            }

            StatusUpdate statusUpdate = new StatusUpdate();
            statusUpdate.setServiceName(serviceName);
            statusUpdate.setService(service);
            statusUpdate.setStatus(status);
            statusUpdate.setStatusTime(OffsetDateTime.now());
            this.statusCode.update(statusUpdate);
        }
    }
}
