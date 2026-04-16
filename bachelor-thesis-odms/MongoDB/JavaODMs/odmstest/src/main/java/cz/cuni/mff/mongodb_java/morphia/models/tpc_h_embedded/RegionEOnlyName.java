package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;

import dev.morphia.annotations.Embedded;

@Embedded
public class RegionEOnlyName {
    private int r_regionkey;
    private String r_name;

    public int get_r_regionkey() {
        return r_regionkey;
    }

    // Morphia needs this no-arg constructor
    public RegionEOnlyName() {}

    public RegionEOnlyName(int r_regionkey,String r_name) {
        this.r_regionkey = r_regionkey;
        this.r_name = r_name;
    }
}
