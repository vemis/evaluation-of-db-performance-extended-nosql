package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

import java.time.LocalDate;

@Entity
public class OrdersEWithCustomerWithNationWithRegion {
    @Id
    private int o_orderkey;

    private LocalDate o_orderdate;

    CustomerEOnlyCNameCNation o_customer;

    public OrdersEWithCustomerWithNationWithRegion() {}

    public OrdersEWithCustomerWithNationWithRegion(int o_orderkey, LocalDate o_orderdate, CustomerEOnlyCNameCNation o_customer) {
        this.o_orderkey = o_orderkey;

        this.o_orderdate = o_orderdate;

        this.o_customer = o_customer;

    }
}
