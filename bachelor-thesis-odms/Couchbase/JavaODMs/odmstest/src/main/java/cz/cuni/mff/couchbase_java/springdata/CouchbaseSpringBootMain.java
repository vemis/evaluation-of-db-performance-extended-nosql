package cz.cuni.mff.couchbase_java.springdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@SpringBootApplication
@EnableCouchbaseRepositories(
        basePackages = "cz.cuni.mff.couchbase_java.springdata.repositories"
)
public class CouchbaseSpringBootMain {
    public static void main(String[] args) {
        SpringApplication.run(CouchbaseSpringBootMain.class, args);
    }
}
