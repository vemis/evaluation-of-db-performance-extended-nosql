package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import org.apache.commons.math3.geometry.euclidean.threed.SubPlane;

import java.util.ArrayList;
import java.util.List;

@Embedded
public class NationE {
    @Indexed
    private int n_nationkey;

    private String n_name;
    private int regionkey;
    private String n_comment;

    private List<CustomerE> customers;
    private List<SupplierE> suppliers;


    public NationE() {}

    public NationE(int n_nationkey, String n_name, int regionkey, String n_comment,
                   List<CustomerE> customers) {
        this.n_nationkey = n_nationkey;
        this.n_name = n_name;
        this.regionkey = regionkey;
        this.n_comment = n_comment;
        this.customers = customers;
    }
}
