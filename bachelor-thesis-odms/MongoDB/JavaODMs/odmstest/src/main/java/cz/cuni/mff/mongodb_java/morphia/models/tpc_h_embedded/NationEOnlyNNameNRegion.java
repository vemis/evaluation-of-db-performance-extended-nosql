package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import java.util.List;

@Embedded
public class NationEOnlyNNameNRegion {

    private int n_nationkey;
    private String n_name;
    private int n_regionkey;


    public int get_n_nationkey() {
        return n_nationkey;
    }

    public int get_n_regionkey() {
        return n_regionkey;
    }

    RegionEOnlyName n_region;

    public NationEOnlyNNameNRegion() {}

    public NationEOnlyNNameNRegion(int n_nationkey, String n_name, int n_regionkey ,RegionEOnlyName n_region) {
        this.n_nationkey = n_nationkey;
        this.n_name = n_name;
        this.n_regionkey = n_regionkey;
        this.n_region = n_region;

    }
}
