package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;


import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Indexed;

import java.util.List;

@Embedded
public class CustomerEOnlyCNameCNation {

    private int c_custkey;
    private String c_name;
    private int c_nationkey;

    NationEOnlyNNameNRegion c_nation;

    public String get_c_name() {
        return c_name;
    }

    public int get_c_custkey() {
        return c_custkey;
    }

    public int get_c_nationkey() {
        return c_nationkey;
    }

    public CustomerEOnlyCNameCNation() {}

    public CustomerEOnlyCNameCNation(int c_custkey,
                                     String c_name,
                                     int c_nationkey,
                                     NationEOnlyNNameNRegion c_nation
                                     ) {
        this.c_custkey = c_custkey;
        this.c_name = c_name;
        this.c_nationkey = c_nationkey;
        this.c_nation = c_nation;
    }
}
