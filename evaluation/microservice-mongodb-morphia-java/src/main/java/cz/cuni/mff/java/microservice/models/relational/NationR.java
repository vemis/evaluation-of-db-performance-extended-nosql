package cz.cuni.mff.java.microservice.models.relational;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

@Entity("nationR")
public class NationR {
    @Id
    private int n_nationkey;
    private String n_name;
    @Indexed
    private int n_regionkey;
    private String n_comment;

    public NationR() {}

    public NationR(int n_nationkey, String n_name, int n_regionkey, String n_comment) {
        this.n_nationkey = n_nationkey;
        this.n_name = n_name;
        this.n_regionkey = n_regionkey;
        this.n_comment = n_comment;
    }
}
