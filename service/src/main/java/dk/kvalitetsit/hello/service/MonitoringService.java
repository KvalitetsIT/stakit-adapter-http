package dk.kvalitetsit.hello.service;

import dk.kvalitetsit.hello.service.model.Monitoring;

import java.util.List;

public class MonitoringService {

    private final List<Monitoring> monitoringList;

    public MonitoringService(List<Monitoring> input) {
        this.monitoringList = input;
    }

    public void loopingMonitors() {
        for (Monitoring monitoring: monitoringList){
            System.out.println(monitoring.getServiceIdentifier());
            System.out.println(monitoring.getServiceName());
            System.out.println(monitoring.getEndpoint());
        }
    }
}
