package cz.cuni.mff.java.microservice.model.embedded;

public class RegionEOnlyName {

    private int r_regionkey;
    private String r_name;

    public RegionEOnlyName() {}

    public RegionEOnlyName(int r_regionkey, String r_name) {
        this.r_regionkey = r_regionkey;
        this.r_name = r_name;
    }

    public int get_r_regionkey() {
        return r_regionkey;
    }
}
