package dk.kvalitetsit.hello.configuration;

import dk.kvalitetsit.hello.service.HelloService;
import dk.kvalitetsit.hello.service.HelloServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class HelloConfiguration{

    @Scheduled(fixedRate = 5000)
    public void printStuffRepeatedly() {
        System.out.print("Does it work? ");
    }

    @Bean
    public HelloService helloService() {
        return new HelloServiceImpl();
    }
}
