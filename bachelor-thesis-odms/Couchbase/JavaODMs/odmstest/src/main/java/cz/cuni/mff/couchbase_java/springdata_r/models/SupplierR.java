package cz.cuni.mff.couchbase_java.springdata_r.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;


@Document
@Scope("spring_scope_r")
@Collection("SupplierR")
public class SupplierR {
    @Id
    private int s_suppkey;
    private String s_name;
    private String s_address;
    //@Indexed
    private String s_nationkey;
    private String s_phone;
    private double s_acctbal;

    private String s_comment;

    public SupplierR() {}

    public SupplierR(int s_suppkey, String s_name, String s_address, String s_nationkey, String s_phone, double s_acctbal, String s_comment) {
        this.s_suppkey = s_suppkey;
        this.s_name = s_name;
        this.s_address = s_address;
        this.s_nationkey = s_nationkey;
        this.s_phone = s_phone;
        this.s_acctbal = s_acctbal;
        this.s_comment = s_comment;
    }


}
