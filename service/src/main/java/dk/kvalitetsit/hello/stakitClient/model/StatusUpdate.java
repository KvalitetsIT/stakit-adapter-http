package dk.kvalitetsit.hello.stakitClient.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class StatusUpdate {

    public enum Status{OK, NOT_OK}
    private String service;
    private Status status;
    @JsonProperty("status-time")
    private OffsetDateTime statusTime;
    @JsonProperty("service-name")
    private String serviceName;
    private String message;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public OffsetDateTime getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(OffsetDateTime statusTime) {
        this.statusTime = statusTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
