package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import java.util.List;

@Entity
public class NationE {
    @Id
    private int n_nationkey;

    private String n_name;
    @Indexed
    private int n_regionkey;
    private String n_comment;

    private List<CustomerE> customers;
    private List<SupplierE> suppliers;

    public int getN_regionkey() {
        return n_regionkey;
    }

    public NationE() {}

    public NationE(int n_nationkey, String n_name, int n_regionkey, String n_comment,
                   List<CustomerE> customers) {
        this.n_nationkey = n_nationkey;
        this.n_name = n_name;
        this.n_regionkey = n_regionkey;
        this.n_comment = n_comment;
        this.customers = customers;
    }
}
