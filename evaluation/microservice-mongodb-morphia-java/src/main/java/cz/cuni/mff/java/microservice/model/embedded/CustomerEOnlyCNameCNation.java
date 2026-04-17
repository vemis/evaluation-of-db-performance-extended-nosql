package cz.cuni.mff.java.microservice.model.embedded;

import dev.morphia.annotations.Embedded;

@Embedded
public class CustomerEOnlyCNameCNation {
    private int c_custkey;
    private String c_name;
    private int c_nationkey;
    NationEOnlyNNameNRegion c_nation;

    public CustomerEOnlyCNameCNation() {}

    public CustomerEOnlyCNameCNation(int c_custkey, String c_name, int c_nationkey,
                                     NationEOnlyNNameNRegion c_nation) {
        this.c_custkey = c_custkey;
        this.c_name = c_name;
        this.c_nationkey = c_nationkey;
        this.c_nation = c_nation;
    }

    public int get_c_custkey() { return c_custkey; }

    public int get_c_nationkey() { return c_nationkey; }
}
