package dk.kvalitetsit.hello.stakitClient.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class StatusUpdate {

    private String service;
    private boolean status;
    @JsonProperty("status-time")
    private String statusTime;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(String statusTime) {
        this.statusTime = statusTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
