package dk.kvalitetsit.hello.stakitClient;

import dk.kvalitetsit.hello.stakitClient.model.StatusUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


public class StatusCode {

    private final Logger logger = LoggerFactory.getLogger(StatusCode.class);

    private final WebClient statusClient;
    private final String apiKey;


    public StatusCode(WebClient.Builder webClientBuilder, String endpoint, String apiKey){
        this.statusClient = webClientBuilder
                .baseUrl(endpoint)
                .build();
        this.apiKey = apiKey;
    }


    public void update(StatusUpdate statusUpdate){
        logger.debug("ServiceName {} - Service {} - Status {}", statusUpdate.getServiceName(), statusUpdate.getService(), statusUpdate.getStatus());
        try {
            statusClient.post()
                    .uri("/v1/status")
                    .header("X-API-KEY", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(statusUpdate))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }catch(Exception exception){
            logger.warn("Caught exception when updating status", exception);
        }

    }
}
