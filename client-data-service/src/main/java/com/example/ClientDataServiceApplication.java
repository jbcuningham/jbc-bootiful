package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

@EnableBinding(Sink.class)
@EnableDiscoveryClient
@SpringBootApplication
public class ClientDataServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(ClientDataServiceApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(ClientDataServiceApplication.class, args);
    }

    @MessageEndpoint
    class ClientProcessor {

        private final ClientRepository clientRepository;

        @ServiceActivator(inputChannel = "input")
        public void acceptNewClient(String firstName) {
            this.clientRepository.save(new Client(firstName));
        }

        @Autowired
        public ClientProcessor(ClientRepository clientRepository) {
            this.clientRepository = clientRepository;
        }
    }

    @Bean
    public CommandLineRunner loadClientsData(ClientRepository repository) {
        return (args) -> {
            // save a couple of clients
            repository.save(new Client("Jack", "Bauer"));
            repository.save(new Client("Chloe", "O'Brian"));
            repository.save(new Client("Kim", "Bauer"));
            repository.save(new Client("David", "Palmer"));
            repository.save(new Client("Michelle", "Dessler"));
            repository.save(new Client("Tufail", "Smith"));
            repository.save(new Client("Jack", "Bauer"));
            repository.save(new Client("Clyde C.", "Frog"));
            repository.save(new Client("Shannon", "Bloodbath"));
            repository.save(new Client("Darren", "El Dorado"));
            repository.save(new Client("Jonathan", "Countryham"));

        };
    }

    @Bean
    public CommandLineRunner loadAccountData(AccountRepository repository) {
        return (args) -> {
            repository.save(new Account("1000", "1"));
        };
    }

}




