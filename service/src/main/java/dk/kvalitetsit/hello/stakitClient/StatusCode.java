package dk.kvalitetsit.hello.stakitClient;

import dk.kvalitetsit.hello.stakitClient.model.StatusUpdate;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


public class StatusCode {

    private final WebClient statusClient;
    private final String apiKey;


    public StatusCode(WebClient.Builder webClientBuilder, String endpoint, String apiKey){
        this.statusClient = webClientBuilder
                .baseUrl(endpoint)
                .build();
        this.apiKey = apiKey;
    }


    public void update(StatusUpdate statusUpdate){
        System.out.println(statusUpdate.getServiceName());
        System.out.println(statusUpdate.getService());
        System.out.println(statusUpdate.isStatus());

        statusClient.post()
                .uri("/v1/status")
                .header("X-API-KEY", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(statusUpdate))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
