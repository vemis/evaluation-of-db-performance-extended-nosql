package cz.cuni.mff.couchbase_java.springdata_e.models;

public class RegionEOnlyName {
    private int r_regionkey;
    private String r_name;

    public int get_r_regionkey() {
        return r_regionkey;
    }

    public RegionEOnlyName() {}

    public RegionEOnlyName(int r_regionkey, String r_name) {
        this.r_regionkey = r_regionkey;
        this.r_name = r_name;
    }
}
