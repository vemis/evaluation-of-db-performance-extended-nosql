package cz.cuni.mff.couchbase_java.springdata_e.models;

public class NationEOnlyNNameNRegion {
    private int n_nationkey;
    private String n_name;
    private int n_regionkey;
    private RegionEOnlyName n_region;

    public int get_n_nationkey() {
        return n_nationkey;
    }

    public int get_n_regionkey() {
        return n_regionkey;
    }

    public NationEOnlyNNameNRegion() {}

    public NationEOnlyNNameNRegion(int n_nationkey, String n_name, int n_regionkey, RegionEOnlyName n_region) {
        this.n_nationkey = n_nationkey;
        this.n_name = n_name;
        this.n_regionkey = n_regionkey;
        this.n_region = n_region;
    }
}
