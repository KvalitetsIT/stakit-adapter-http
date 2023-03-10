package dk.kvalitetsit.hello.configuration;

import dk.kvalitetsit.hello.service.*;
import dk.kvalitetsit.hello.service.model.ConfigurationModel;
import dk.kvalitetsit.hello.stakitClient.StatusCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileNotFoundException;

@Configuration
@EnableScheduling
public class HttpAdapterConfiguration {

    @Bean
    public MonitoringService monitoringService(ConfigurationModel input, HttpMonitorService httpMonitorService, StatusCode statusCode) {
        return new MonitoringService(input, httpMonitorService, statusCode);
    }

    @Bean
    public ConfigurationModel configurationModel(@Value("${CONFIGURATION_FILE}") String filePath) throws FileNotFoundException {
        ConfigurationModel model = new ConfigurationReader().readConfiguration(filePath);
        System.setProperty("FREQUENCY", model.getFrequency());
        return model;
    }

    @Bean
    public HttpMonitorService httpMonitorService(){
        return new HttpMonitorService();
    }

    @Bean
    public StatusCode statusCode(WebClient.Builder webClientBuilder, ConfigurationModel configurationModel){
        return new StatusCode(webClientBuilder, configurationModel.getStakit().getEndpoint(), configurationModel.getStakit().getApiKey());
    }
}
