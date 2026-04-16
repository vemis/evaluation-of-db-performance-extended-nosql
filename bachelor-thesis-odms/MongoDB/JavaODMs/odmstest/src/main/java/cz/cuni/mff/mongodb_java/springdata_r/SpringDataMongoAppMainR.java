package cz.cuni.mff.mongodb_java.springdata_r;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class SpringDataMongoAppMainR {
    public static void main(String[] args) {
        SpringApplication.run(SpringDataMongoAppMainR.class, args);
    }
}
