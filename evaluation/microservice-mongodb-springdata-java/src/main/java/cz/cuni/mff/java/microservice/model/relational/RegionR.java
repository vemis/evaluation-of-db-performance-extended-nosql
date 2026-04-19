package cz.cuni.mff.java.microservice.model.relational;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "regionR")
public class RegionR {
    @Id
    private int r_regionkey;
    private String r_name;
    private String r_comment;

    public RegionR() {}

    public RegionR(int r_regionkey, String r_name, String r_comment) {
        this.r_regionkey = r_regionkey;
        this.r_name = r_name;
        this.r_comment = r_comment;
    }
}
