package cz.cuni.mff.couchbase_java.springdata_r.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

@Document
@Scope("spring_scope_r")
@Collection("PartsuppR")
public class PartsuppR {
    @Id
    private String ps_id;
    //@Indexed
    private String ps_partkey;
    //@Indexed
    private String ps_suppkey;

    private int ps_availqty;
    private double ps_supplycost;
    private String ps_comment;

    public PartsuppR() {}

    public PartsuppR(String ps_partkey, String ps_suppkey, int ps_availqty, double ps_supplycost, String ps_comment) {
        this.ps_id = ps_partkey + "|" + ps_suppkey;
        this.ps_partkey = ps_partkey;
        this.ps_suppkey = ps_suppkey;
        this.ps_availqty = ps_availqty;
        this.ps_supplycost = ps_supplycost;
        this.ps_comment = ps_comment;
    }
}
