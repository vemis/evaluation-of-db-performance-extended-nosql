package cz.cuni.mff.java.microservice.models.relational;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("partR")
public class PartR {
    @Id
    private int p_partkey;
    private String p_name;
    private String p_mfgr;
    private String p_brand;
    private String p_type;
    private int p_size;
    private String p_container;
    private double p_retailprice;
    private String p_comment;

    public PartR() {}

    public PartR(int p_partkey, String p_name, String p_mfgr, String p_brand,
                 String p_type, int p_size, String p_container,
                 double p_retailprice, String p_comment) {
        this.p_partkey = p_partkey;
        this.p_name = p_name;
        this.p_mfgr = p_mfgr;
        this.p_brand = p_brand;
        this.p_type = p_type;
        this.p_size = p_size;
        this.p_container = p_container;
        this.p_retailprice = p_retailprice;
        this.p_comment = p_comment;
    }
}
