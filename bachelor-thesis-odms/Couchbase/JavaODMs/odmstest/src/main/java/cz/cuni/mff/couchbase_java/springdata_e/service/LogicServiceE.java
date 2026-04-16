package cz.cuni.mff.couchbase_java.springdata_e.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogicServiceE {
    private final CouchbaseTemplate couchbaseTemplate;

    @Autowired
    public LogicServiceE(CouchbaseTemplate couchbaseTemplate) {
        this.couchbaseTemplate = couchbaseTemplate;
    }

    public CouchbaseTemplate getCouchbaseTemplate() {
        return couchbaseTemplate;
    }


}
