package cz.cuni.mff.java.microservice.model.relational;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

@Document
@Scope("spring_scope_r")
@Collection("CustomerR")
public class CustomerROrdersR {

    @Id
    private String id;
    private String cName;
    private String oOrderDate;
    private double oTotalPrice;

    public CustomerROrdersR() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCName() { return cName; }
    public void setCName(String cName) { this.cName = cName; }

    public String getOOrderDate() { return oOrderDate; }
    public void setOOrderDate(String oOrderDate) { this.oOrderDate = oOrderDate; }

    public double getOTotalPrice() { return oTotalPrice; }
    public void setOTotalPrice(double oTotalPrice) { this.oTotalPrice = oTotalPrice; }
}
