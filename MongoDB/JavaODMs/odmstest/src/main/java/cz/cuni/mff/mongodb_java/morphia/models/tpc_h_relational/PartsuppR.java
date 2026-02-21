package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

@Entity
public class PartsuppR {
    @Id
    private String ps_id;
    @Indexed
    private int ps_partKey;
    @Indexed
    private int ps_suppKey;

    private int ps_availqty;
    private double ps_supplycost;
    private String ps_comment;

    public PartsuppR() {}

    public PartsuppR( int ps_partKey, int ps_suppKey, int ps_availqty, double ps_supplycost, String ps_comment) {
        this.ps_id = Integer.toString(ps_partKey) + "|" + Integer.toString(ps_suppKey);
        this.ps_partKey = ps_partKey;
        this.ps_suppKey = ps_suppKey;
        this.ps_availqty = ps_availqty;
        this.ps_supplycost = ps_supplycost;
        this.ps_comment = ps_comment;
    }
}
