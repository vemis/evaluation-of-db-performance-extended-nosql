package cz.cuni.mff.couchbase_java.springdata_r.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.couchbase.core.CouchbaseTemplate;

@Service
public class LogicServiceR {
    private final CouchbaseTemplate couchbaseTemplate;

    @Autowired
    public LogicServiceR(CouchbaseTemplate couchbaseTemplate) {
        this.couchbaseTemplate = couchbaseTemplate;
    }

    public CouchbaseTemplate getCouchbaseTemplate() {
        return couchbaseTemplate;
    }


}
