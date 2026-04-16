package cz.cuni.mff.mongodb_java.morphia.models.tpc_h_embedded;

import cz.cuni.mff.mongodb_java.morphia.models.Employee;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RegionE {
    @Id
    //private ObjectId r_regionkey;
    private int r_regionkey;

    private String r_name;
    private String r_comment;

    //@Reference
    private List<NationE> nations;

    // Morphia needs this no-arg constructor
    public RegionE() {}

    public RegionE(int r_regionkey, String r_name, String r_comment, List<NationE> nations) {
        this.r_regionkey = r_regionkey;
        this.r_name = r_name;
        this.r_comment = r_comment;
        this.nations = nations;
    }
}
