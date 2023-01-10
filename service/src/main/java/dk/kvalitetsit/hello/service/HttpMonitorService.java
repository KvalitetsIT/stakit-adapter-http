package dk.kvalitetsit.hello.service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpMonitorService {


    public boolean getResult(String url) throws IOException, InterruptedException {
        return findResult(url);
    }

    private boolean findResult(String url) throws IOException, InterruptedException {
        boolean success;
        try{
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();

            success = firstDigit(statusCode) == 2;
        }catch(ConnectException | IllegalArgumentException | URISyntaxException conEx){
            success = false;
        }
        return success;
    }

    private int firstDigit(int n) {
        while (n >= 10)
            n /= 10;
        return n;
    }
}
