package dk.kvalitetsit.hello.service;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigurationReaderTest {

    private ConfigurationReader configurationReader;

    @Before
    public void setup() {
        configurationReader = new ConfigurationReader();
    }

    @Test
    public void monitorTest() throws FileNotFoundException {
        //System.out.println(Paths.get(".").toAbsolutePath());

        var input = "src/test/resources/TestConfiguration.yaml";

        var result = configurationReader.readConfiguration(input);
        assertNotNull(result);
        assertNotNull(result.getStakit());
        assertEquals("https://backend.dk/", result.getStakit().getEndpoint());
        assertEquals("MY-SUPE-SECRET-KEY", result.getStakit().getApiKey());

        assertEquals("PT5M", result.getFrequency());

        assertNotNull(result.getMonitoring());
        assertEquals("class java.util.ArrayList", result.getMonitoring().getClass().toString());
        assertEquals(2, result.getMonitoring().size());
        assertEquals("unikt id", result.getMonitoring().get(0).getServiceIdentifier());
        assertEquals("Noget andet navn", result.getMonitoring().get(1).getServiceName());
        assertEquals("http://en anden service", result.getMonitoring().get(1).getEndpoint());
    }
}
