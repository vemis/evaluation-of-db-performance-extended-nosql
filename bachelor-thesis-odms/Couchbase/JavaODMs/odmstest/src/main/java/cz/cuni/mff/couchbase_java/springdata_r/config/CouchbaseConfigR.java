package cz.cuni.mff.couchbase_java.springdata_r.config;

import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.java.http.CouchbaseHttpClient;
import com.couchbase.client.java.transactions.config.TransactionsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.convert.CouchbaseConverter;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

import java.time.Duration;

@Configuration
//@EnableCouchbaseRepositories(basePackages = "cz.cuni.mff.couchbase_java.springdata_r")
public class CouchbaseConfigR extends AbstractCouchbaseConfiguration {
    @Override
    public String getConnectionString() {
        return "127.0.0.1";
    }

    @Override
    public String getUserName() {
        return "Administrator";
    }

    @Override
    public String getPassword() {
        return "password";
    }

    @Override
    public String getBucketName() {
        // bucket needs to be created beforehand
        // or via the Couchbase Java SDK
        return "spring_bucket_r";
    }

    // Customization of transaction behavior is via the configureEnvironment() method
    @Override
    protected void configureEnvironment(final ClusterEnvironment.Builder builder) {
        builder.timeoutConfig().queryTimeout(Duration.ofMinutes(5));
        builder.timeoutConfig().kvTimeout(Duration.ofMinutes(5));
        //builder.transactionsConfig(
        //        TransactionsConfig.builder().timeout(Duration.ofSeconds(30)));
    }





/*
    @Bean
    public CouchbaseTemplate couchbaseTemplate(
            CouchbaseClientFactory couchbaseClientFactory,
            CouchbaseConverter couchbaseConverter) {

        return new CouchbaseTemplate(couchbaseClientFactory, couchbaseConverter);
    }*/


}
