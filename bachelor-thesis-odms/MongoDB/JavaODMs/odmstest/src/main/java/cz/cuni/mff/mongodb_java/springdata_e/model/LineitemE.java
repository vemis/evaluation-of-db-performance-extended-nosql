package cz.cuni.mff.mongodb_java.springdata_e.model;

import java.time.LocalDate;

public class LineitemE {

    // does not exist, should be composite key
    private String l_id;
    private int l_orderkey;
    private int l_partkey;
    private int l_suppkey;
    private String l_ps_id;

    private int l_linenumber;
    private int l_quantity;

    private double l_extendedprice;
    private double l_discount;
    private double l_tax;
    private String l_returnflag;
    private String l_linestatus;
    private LocalDate l_shipdate;
    private LocalDate l_commitdate;
    private LocalDate l_receiptdate;
    private String l_shipinstruct;
    private String l_shipmode;
    private String l_comment;

    public LineitemE() {}

    public LineitemE(int l_orderkey, int l_partkey, int l_suppkey, int l_linenumber, int l_quantity,
                     double l_extendedprice, double l_discount, double l_tax,
                     String l_returnflag, String l_linestatus,
                     LocalDate l_shipdate, LocalDate l_commitdate, LocalDate l_receiptdate,
                     String l_shipinstruct, String l_shipmode, String l_comment) {
        this.l_ps_id = Integer.toString(l_partkey) + "|" + Integer.toString(l_suppkey);
        this.l_id = Integer.toString(l_orderkey) + Integer.toString(l_linenumber);
        this.l_orderkey = l_orderkey;
        this.l_partkey = l_partkey;
        this.l_suppkey = l_suppkey;
        this.l_linenumber = l_linenumber;
        this.l_quantity = l_quantity;
        this.l_extendedprice = l_extendedprice;
        this.l_discount = l_discount;
        this.l_tax = l_tax;
        this.l_returnflag = l_returnflag;
        this.l_linestatus = l_linestatus;
        this.l_shipdate = l_shipdate;
        this.l_commitdate = l_commitdate;
        this.l_receiptdate = l_receiptdate;
        this.l_shipinstruct = l_shipinstruct;
        this.l_shipmode = l_shipmode;
        this.l_comment = l_comment;
    }

    public int get_l_orderkey() {
        return l_orderkey;
    }
}
