package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

import java.time.LocalDate;
import java.util.List;

@Entity
public class OrdersEOnlyOCustomer {
    @Id
    private int o_orderkey;

    CustomerEOnlyCNameCNation o_customer;

    public OrdersEOnlyOCustomer() {}

    public OrdersEOnlyOCustomer(int o_orderkey,  CustomerEOnlyCNameCNation o_customer) {
        this.o_orderkey = o_orderkey;
        this.o_customer = o_customer;
    }
}
