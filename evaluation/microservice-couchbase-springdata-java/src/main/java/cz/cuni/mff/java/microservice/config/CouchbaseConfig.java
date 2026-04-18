package cz.cuni.mff.java.microservice.config;

import com.couchbase.client.java.env.ClusterEnvironment;
import org.springframework.boot.autoconfigure.couchbase.ClusterEnvironmentBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CouchbaseConfig {

    @Bean
    public ClusterEnvironmentBuilderCustomizer couchbaseTimeoutCustomizer() {
        return builder -> builder.timeoutConfig()
                .connectTimeout(Duration.ofSeconds(30))
                .queryTimeout(Duration.ofMinutes(5))
                .kvTimeout(Duration.ofMinutes(5));
    }
}
