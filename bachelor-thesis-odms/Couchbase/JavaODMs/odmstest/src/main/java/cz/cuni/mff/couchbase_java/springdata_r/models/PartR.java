package cz.cuni.mff.couchbase_java.springdata_r.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

@Document
@Scope("spring_scope_r")
@Collection("PartR")
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
    private String p_commen;

    public PartR() {}

    public PartR(int p_partkey, String p_name, String p_mfgr, String p_brand, String p_type, int p_size, String p_container, double p_retailprice, String p_commen) {
        this.p_partkey = p_partkey;
        this.p_name = p_name;
        this.p_mfgr = p_mfgr;
        this.p_brand = p_brand;
        this.p_type = p_type;
        this.p_size = p_size;
        this.p_container = p_container;
        this.p_retailprice = p_retailprice;
        this.p_commen = p_commen;
    }
}
