package cz.cuni.mff.couchbase_java.springdata_e.models;

import com.couchbase.client.java.Cluster;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

import java.time.LocalDate;

@Document
@Scope("spring_scope_e")
@Collection("OrdersEWithCustomerWithNationWithRegion")
public class OrdersEWithCustomerWithNationWithRegion {
    @Id
    private int o_orderkey;

    private LocalDate o_orderdate;

    private CustomerEOnlyCNameCNation o_customer;

    public OrdersEWithCustomerWithNationWithRegion() {}

    public OrdersEWithCustomerWithNationWithRegion(int o_orderkey, LocalDate o_orderdate, CustomerEOnlyCNameCNation o_customer) {
        this.o_orderkey = o_orderkey;
        this.o_orderdate = o_orderdate;
        this.o_customer = o_customer;
    }

    public static void createIndexes(Cluster cluster) {
        return;
    }
}
