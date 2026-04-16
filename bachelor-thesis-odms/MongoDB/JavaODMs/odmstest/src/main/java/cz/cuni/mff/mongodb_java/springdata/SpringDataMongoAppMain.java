package cz.cuni.mff.mongodb_java.springdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories("cz.cuni.mff.mongodb_java.springdata.repositories")
public class SpringDataMongoAppMain {
    public static void main(String[] args) {
        SpringApplication.run(SpringDataMongoAppMain.class, args);


    }
}

