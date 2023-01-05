package dk.kvalitetsit.hello.service.model;

import java.util.List;

public class ConfigurationModel {
    private Stakit stakit;

    private String frequency;
    private List<Monitoring> monitoring;


    public Stakit getStakit() {
        return stakit;
    }

    public void setStakit(Stakit stakit) {
        this.stakit = stakit;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public List<Monitoring> getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(List<Monitoring> monitoring) {
        this.monitoring = monitoring;
    }

}
