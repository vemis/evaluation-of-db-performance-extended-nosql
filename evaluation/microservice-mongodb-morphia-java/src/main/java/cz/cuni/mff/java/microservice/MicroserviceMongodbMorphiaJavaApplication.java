package cz.cuni.mff.java.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroserviceMongodbMorphiaJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceMongodbMorphiaJavaApplication.class, args);
    }
}
