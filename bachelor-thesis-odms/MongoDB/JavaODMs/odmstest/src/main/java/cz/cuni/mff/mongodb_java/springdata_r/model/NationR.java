package cz.cuni.mff.mongodb_java.springdata_r.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "nationR")
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
