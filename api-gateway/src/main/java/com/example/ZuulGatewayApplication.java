package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

// interface Source {
//    String OUTPUT = "output";
//
//    @Output("output")
//    MessageChannel output();
//}



@EnableBinding(Source.class )
@EnableFeignClients
@EnableCircuitBreaker
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class ZuulGatewayApplication {

    private static final Logger log = LoggerFactory.getLogger(ZuulGatewayApplication.class);

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }



    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }
}

@RestController
@RequestMapping("/clients")
class ClientApiGatewayRestController {

    private final RestTemplate restTemplate;

    @Autowired
    public ClientApiGatewayRestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Collection<String> fallback() {
        return new ArrayList<>();
    }

    @HystrixCommand(fallbackMethod = "fallback")
    @RequestMapping(method = RequestMethod.GET, value = "/names")
    public Collection<String> names() {
        return this.restTemplate.exchange("http://client-data/clients",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Resources<Client>>() {
                })
                .getBody()
                .getContent()
                .stream()
                .map(Client::getFirstName)
                .collect(Collectors.toList());
    }

    @Autowired
    private Source outputChannelSource;

    @RequestMapping(method = RequestMethod.POST)
    public void write(@RequestBody Client client) {
        MessageChannel channel = this.outputChannelSource.output();
        channel.send(
                MessageBuilder.withPayload(client.getFirstName()).build()

        );
    }


}

@JsonIgnoreProperties(ignoreUnknown = true)
class Client {
    private String firstName;

    public Client() {
    }

    public Client(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }
}
