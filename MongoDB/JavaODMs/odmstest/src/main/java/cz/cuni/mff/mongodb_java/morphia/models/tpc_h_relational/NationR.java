package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_relational;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import java.util.ArrayList;

@Entity
public class NationR {
    @Id
    private int n_nationkey;

    private String n_name;

    @Indexed
    private int n_regionkey; //foreign key

    private String n_comment;

    public NationR() {}

    public NationR(int n_nationkey, String n_name, int n_regionkey, String n_comment) {
        this.n_nationkey = n_nationkey;
        this.n_name = n_name;
        this.n_regionkey = n_regionkey;
        this.n_comment = n_comment;
    }
}
