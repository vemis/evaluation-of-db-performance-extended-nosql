package cz.cuni.mff.couchbase_java;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

public class SpringDataCouchbaseClusterManagement {
    public static void createScope(String scopeName, Bucket bucket) {
        try {
            bucket.collections().createScope(scopeName);
        } catch (Exception ignored) {
            ignored.printStackTrace();
            System.out.println(scopeName + " already exists");
        }
    }

    public static void createCollection(String scopeName, String collectionName, Bucket bucket) {
        try {
            bucket.collections().createCollection(scopeName, collectionName);
        } catch (Exception ignored) {
            ignored.printStackTrace();
            System.out.println(scopeName + "." + collectionName + " already exists");
        }
    }

    public static void createIndex(Cluster cluster, String bucketName, String scopeName, String collectionName, String fieldName) {
        String queryStr = "CREATE INDEX IF NOT EXISTS idx_" + collectionName + "_" + fieldName
                + " ON " + bucketName + "." + scopeName + "." + collectionName + "(" + fieldName +")";
        System.out.println("Query: " + queryStr);

        cluster.query(queryStr);
    }
}
