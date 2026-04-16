package cz.cuni.mff.couchbase_java.springdata_e;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

//@SpringBootApplication(scanBasePackages = "cz.cuni.mff.couchbase_java.springdata_r")
@SpringBootApplication
@EnableCouchbaseRepositories(
        basePackages = "cz.cuni.mff.couchbase_java.springdata_e"
)
public class CouchbaseSpringBootMainE {
    public static void main(String[] args) {
        SpringApplication.run(CouchbaseSpringBootMainE.class, args);
    }
}
