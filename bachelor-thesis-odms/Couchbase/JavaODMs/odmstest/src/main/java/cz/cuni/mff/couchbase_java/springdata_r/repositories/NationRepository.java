package cz.cuni.mff.couchbase_java.springdata_r.repositories;

import cz.cuni.mff.couchbase_java.springdata_r.models.CustomerR;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NationRepository extends CouchbaseRepository<CustomerR, String> {
}
