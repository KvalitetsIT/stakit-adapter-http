package dk.kvalitetsit.hello.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpMonitorService {

    private final Logger logger = LoggerFactory.getLogger(HttpMonitorService.class);

    public boolean getResult(String url) {
        return findResult(url);
    }

    private boolean findResult(String url) {
        boolean success;
        try{
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();

            success = firstDigit(statusCode) == 2;
            logger.debug("No exceptions caught, status code is {}", statusCode);
        }catch(IllegalArgumentException | URISyntaxException | IOException | InterruptedException exception){
            success = false;
            logger.info("Caught exception when finding status code", exception);
        }
        return success;
    }

    private int firstDigit(int n) {
        while (n >= 10){
            n /= 10;
        }
        return n;
    }
}
