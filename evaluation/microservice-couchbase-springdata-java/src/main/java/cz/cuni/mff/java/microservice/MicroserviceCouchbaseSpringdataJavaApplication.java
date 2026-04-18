package cz.cuni.mff.java.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroserviceCouchbaseSpringdataJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceCouchbaseSpringdataJavaApplication.class, args);
    }
}
