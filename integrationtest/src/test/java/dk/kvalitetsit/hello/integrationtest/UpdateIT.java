package dk.kvalitetsit.hello.integrationtest;

import org.junit.Test;
import org.mockserver.model.RegexBody;
import org.mockserver.verify.VerificationTimes;

import static org.mockserver.model.HttpRequest.request;


public class UpdateIT extends AbstractIntegrationTest {


    @Test
    public void verifyMockServersGetCalled() throws InterruptedException {
        Thread.sleep(3000);

        getMockServerUpdateClient().verify(request().withPath("/is/ok").withMethod("GET"), VerificationTimes.exactly(1));
        getMockServerUpdateClient().verify(request().withPath("/is/not/ok").withMethod("GET"), VerificationTimes.exactly(1));

        getMockServerBackendClient().verify(request().withPath("/v1/status").withMethod("POST")
                .withBody(new RegexBody("\\{\"service\":\"unikt id\",\"status\":\"OK\",\"message\":null,\"status-time\":\"20..-..-..T..:..:.*\",\"service-name\":\"Læsbart navn\"}")), VerificationTimes.atLeast(1));
        getMockServerBackendClient().verify(request().withPath("/v1/status").withMethod("POST")
                .withBody(new RegexBody("\\{\"service\":\"andet unikt id\",\"status\":\"NOT_OK\",\"message\":null,\"status-time\":\"20..-..-..T..:..:.*\",\"service-name\":\"Noget andet navn\"}")), VerificationTimes.atLeast(1));


        Thread.sleep(15000);

        getMockServerUpdateClient().verify(request().withPath("/is/ok").withMethod("GET"), VerificationTimes.exactly(4));
        getMockServerUpdateClient().verify(request().withPath("/is/not/ok").withMethod("GET"), VerificationTimes.exactly(4));

        getMockServerBackendClient().verify(request().withPath("/v1/status").withMethod("POST")
                .withBody(new RegexBody("\\{\"service\":\"unikt id\",\"status\":\"OK\",\"message\":null,\"status-time\":\"20..-..-..T..:..:.*\",\"service-name\":\"Læsbart navn\"}")), VerificationTimes.atLeast(4));
        getMockServerBackendClient().verify(request().withPath("/v1/status").withMethod("POST")
                .withBody(new RegexBody("\\{\"service\":\"andet unikt id\",\"status\":\"NOT_OK\",\"message\":null,\"status-time\":\"20..-..-..T..:..:.*\",\"service-name\":\"Noget andet navn\"}")), VerificationTimes.atLeast(4));
    }
}