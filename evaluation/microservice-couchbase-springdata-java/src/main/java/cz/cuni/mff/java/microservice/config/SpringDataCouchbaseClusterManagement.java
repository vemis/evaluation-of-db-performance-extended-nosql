package cz.cuni.mff.java.microservice.config;

import com.couchbase.client.java.Cluster;

public class SpringDataCouchbaseClusterManagement {

    public static void createIndex(Cluster cluster, String bucketName, String scopeName, String collectionName, String fieldName) {
        String indexName = "idx_" + collectionName + "_" + fieldName;
        String query = "CREATE INDEX IF NOT EXISTS `" + indexName + "`"
                + " ON `" + bucketName + "`.`" + scopeName + "`.`" + collectionName + "`(" + fieldName + ")";
        cluster.query(query);
    }
}
