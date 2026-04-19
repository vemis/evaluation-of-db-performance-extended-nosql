package cz.cuni.mff.java.microservice.model.embedded;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "ordersEWithCustomerWithNationWithRegion")
public class OrdersEWithCustomerWithNationWithRegion {
    @Id
    private int o_orderkey;
    private LocalDate o_orderdate;
    private CustomerEOnlyCNameCNation o_customer;

    public OrdersEWithCustomerWithNationWithRegion() {}

    public OrdersEWithCustomerWithNationWithRegion(int o_orderkey, LocalDate o_orderdate,
                                                   CustomerEOnlyCNameCNation o_customer) {
        this.o_orderkey = o_orderkey;
        this.o_orderdate = o_orderdate;
        this.o_customer = o_customer;
    }
}
